package com.example.android.ui.screens

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.BuildConfig
import com.example.android.data.model.LiveSessionRequest
import com.example.android.data.network.RetrofitClient
import com.example.android.webrtc.MqttSignalingClient
import com.example.android.webrtc.SignalingMessage
import com.example.android.webrtc.WebRtcClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import org.webrtc.VideoTrack

// ─── UI 상태 ──────────────────────────────────────────────────────────────────

enum class LiveConnectionState {
    IDLE,        // 초기
    LOADING,     // 세션 발급 중
    CONNECTING,  // MQTT 연결 + WebRTC 시그널링
    STREAMING,   // 영상 수신 중
    ERROR,
    DISCONNECTED
}

data class LiveUiState(
    val connectionState: LiveConnectionState = LiveConnectionState.IDLE,
    val errorMessage: String? = null
)

// ─── ViewModel ────────────────────────────────────────────────────────────────

class LiveViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "LiveViewModel"
    }

    private val api = RetrofitClient.apiService

    private val _uiState = MutableStateFlow(LiveUiState())
    val uiState: StateFlow<LiveUiState> = _uiState.asStateFlow()

    private val _videoTrack = MutableStateFlow<VideoTrack?>(null)
    val videoTrack: StateFlow<VideoTrack?> = _videoTrack.asStateFlow()

    private var webRtcClient: WebRtcClient? = null
    private var signalingClient: MqttSignalingClient? = null

    // ─── 공개 인터페이스 ──────────────────────────────────────────────────────

    /** LiveScreen이 표시될 때 호출 */
    fun startStream() {
        if (_uiState.value.connectionState != LiveConnectionState.IDLE &&
            _uiState.value.connectionState != LiveConnectionState.ERROR &&
            _uiState.value.connectionState != LiveConnectionState.DISCONNECTED
        ) return

        viewModelScope.launch { fetchSessionAndConnect() }
    }

    /** LiveScreen이 사라질 때 호출 */
    fun stopStream() {
        signalingClient?.publish(SignalingMessage.Bye)
        cleanup()
        setState(LiveConnectionState.DISCONNECTED)
    }

    // ─── 세션 발급 → MQTT 연결 → WebRTC 오퍼 ─────────────────────────────────

    private suspend fun fetchSessionAndConnect() {
        setState(LiveConnectionState.LOADING)

        // 1. 홈 API에서 첫 번째 deviceId 획득
        val deviceId = runCatching {
            val homeRes = api.getHome()
            homeRes.body()?.data?.devices?.firstOrNull()?.deviceId
        }.getOrNull()

        if (deviceId == null) {
            setError("연결할 장치를 찾을 수 없습니다")
            return
        }

        // 2. 라이브 세션 발급
        val session = runCatching {
            val res = api.createLiveSession(LiveSessionRequest(deviceId))
            if (res.isSuccessful && res.body()?.success == true) res.body()!!.data
            else null
        }.getOrNull()

        if (session == null) {
            setError("세션 발급에 실패했습니다")
            return
        }

        Log.d(TAG, "Session created: ${session.sessionId}, device: ${session.deviceSerial}")
        setState(LiveConnectionState.CONNECTING)

        // 3. WebRTC 클라이언트 초기화 (메인 스레드)
        withContext(Dispatchers.Main) {
            initWebRtc()
        }

        // 4. MQTT 시그널링 연결
        connectSignaling(session.deviceSerial, session.sessionId)
    }

    private fun initWebRtc() {
        webRtcClient = WebRtcClient(
            context         = getApplication(),
            onIceCandidate  = { candidate -> publishIceCandidate(candidate) },
            onConnected     = { setState(LiveConnectionState.STREAMING) },
            onVideoTrack    = { track ->
                Log.d(TAG, "Video track received, enabling")
                track.setEnabled(true)
                _videoTrack.value = track
            }
        ).also { it.initialize() }
    }

    private fun connectSignaling(deviceSerial: String, sessionId: String) {
        signalingClient = MqttSignalingClient(
            brokerUrl    = BuildConfig.MQTT_BROKER_URL,
            deviceSerial = deviceSerial,
            sessionId    = sessionId
        ).apply {
            onConnected = {
                Log.d(TAG, "MQTT connected, creating WebRTC offer")
                viewModelScope.launch(Dispatchers.Main) { createAndSendOffer() }
            }
            onMessage = { msg ->
                viewModelScope.launch(Dispatchers.Main) { handleSignalingMessage(msg) }
            }
            onError = { error ->
                Log.e(TAG, "Signaling error: $error")
                setError(error)
            }
            connect()
        }
    }

    // ─── WebRTC 시그널링 처리 ──────────────────────────────────────────────────

    private fun createAndSendOffer() {
        webRtcClient?.createOffer(
            onSuccess = { sdp ->
                Log.d(TAG, "Offer created, publishing via MQTT")
                signalingClient?.publish(SignalingMessage.Offer(sdp.description))
            },
            onFailure = { error -> setError("Offer 생성 실패: $error") }
        )
    }

    private fun handleSignalingMessage(msg: SignalingMessage) {
        when (msg) {
            is SignalingMessage.Answer -> {
                Log.d(TAG, "Answer received")
                val sdp = SessionDescription(SessionDescription.Type.ANSWER, msg.sdp)
                webRtcClient?.setRemoteDescription(
                    sdp       = sdp,
                    onSuccess = { Log.d(TAG, "Remote description set") },
                    onFailure = { error -> setError("Answer 처리 실패: $error") }
                )
            }

            is SignalingMessage.IceCandidate -> {
                Log.d(TAG, "Remote ICE candidate received")
                val candidate = IceCandidate(msg.sdpMid, msg.sdpMLineIndex, msg.candidate)
                webRtcClient?.addIceCandidate(candidate)
            }

            is SignalingMessage.Error -> {
                val detail = when (msg.reason) {
                    "max_sessions_exceeded" ->
                        "최대 동시 접속 수(${msg.limit})를 초과했습니다 (현재 ${msg.current}개 연결 중)"
                    else -> "보드 오류: ${msg.reason}"
                }
                Log.e(TAG, "Signaling error from board: $detail")
                setError(detail)
            }

            is SignalingMessage.Bye -> {
                Log.d(TAG, "Bye received from board")
                cleanup()
                setState(LiveConnectionState.DISCONNECTED)
            }

            is SignalingMessage.Offer -> {
                // 앱은 answerer가 아니므로 무시
                Log.w(TAG, "Unexpected offer received (ignoring)")
            }
        }
    }

    private fun publishIceCandidate(candidate: IceCandidate) {
        signalingClient?.publish(
            SignalingMessage.IceCandidate(
                candidate      = candidate.sdp,
                sdpMid         = candidate.sdpMid ?: "",
                sdpMLineIndex  = candidate.sdpMLineIndex
            )
        )
    }

    // ─── 상태 관리 ────────────────────────────────────────────────────────────

    private fun setState(state: LiveConnectionState) {
        _uiState.value = LiveUiState(connectionState = state)
    }

    private fun setError(message: String) {
        Log.e(TAG, "Error: $message")
        _uiState.value = LiveUiState(
            connectionState = LiveConnectionState.ERROR,
            errorMessage    = message
        )
    }

    private fun cleanup() {
        _videoTrack.value = null
        signalingClient?.disconnect()
        signalingClient = null
        webRtcClient?.release()
        webRtcClient = null
    }

    override fun onCleared() {
        super.onCleared()
        cleanup()
    }

    /** VideoPlayerCard의 SurfaceViewRenderer 초기화에 필요한 EGL context */
    fun getEglBaseContext(): org.webrtc.EglBase.Context? = webRtcClient?.eglBase?.eglBaseContext
}
