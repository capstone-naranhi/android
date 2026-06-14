package com.example.android.webrtc

import android.content.Context
import android.util.Log
import org.webrtc.DataChannel
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.MediaStreamTrack
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.RendererCommon
import org.webrtc.RtpTransceiver
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoTrack

/**
 * WebRTC PeerConnection 생성 및 라이프사이클 관리.
 * 보드(Jetson)로부터 단방향 영상 수신만 담당합니다.
 *
 * @param onIceCandidate 로컬 ICE candidate 생성 시 → MQTT로 발행해야 함
 * @param onConnected    PeerConnection 연결 완료 시
 * @param onVideoTrack   원격 VideoTrack 수신 시 → SurfaceViewRenderer에 연결
 */
class WebRtcClient(
    private val context: Context,
    private val onIceCandidate: (IceCandidate) -> Unit,
    private val onConnected: () -> Unit,
    private val onVideoTrack: (VideoTrack) -> Unit
) {
    companion object {
        private const val TAG = "WebRtcClient"
        private val STUN_SERVERS = listOf(
            PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer(),
            PeerConnection.IceServer.builder("stun:stun1.l.google.com:19302").createIceServer()
        )
    }

    val eglBase: EglBase = EglBase.create()

    private var factory: PeerConnectionFactory? = null
    private var peerConnection: PeerConnection? = null

    // ─── 초기화 ────────────────────────────────────────────────────────────────

    fun initialize() {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context)
                .setEnableInternalTracer(false)
                .createInitializationOptions()
        )

        factory = PeerConnectionFactory.builder()
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBase.eglBaseContext))
            .setVideoEncoderFactory(
                DefaultVideoEncoderFactory(eglBase.eglBaseContext, true, true)
            )
            .createPeerConnectionFactory()

        peerConnection = createPeerConnection()
    }

    private fun createPeerConnection(): PeerConnection? {
        val rtcConfig = PeerConnection.RTCConfiguration(STUN_SERVERS).apply {
            sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
            // PeerConnection 생성 즉시 ICE 후보 수집 시작 → createOffer 이후 수집 대기 시간 단축
            iceCandidatePoolSize = 2
        }

        return factory?.createPeerConnection(rtcConfig, object : PeerConnection.Observer {
            override fun onIceCandidate(candidate: IceCandidate) {
                Log.d(TAG, "Local ICE candidate: ${candidate.sdp}")
                this@WebRtcClient.onIceCandidate(candidate)
            }

            override fun onIceConnectionChange(state: PeerConnection.IceConnectionState) {
                Log.d(TAG, "ICE connection state: $state")
                if (state == PeerConnection.IceConnectionState.CONNECTED ||
                    state == PeerConnection.IceConnectionState.COMPLETED
                ) {
                    onConnected()
                }
            }

            override fun onTrack(transceiver: RtpTransceiver) {
                val track = transceiver.receiver.track()
                if (track is VideoTrack) {
                    Log.d(TAG, "Remote video track received")
                    onVideoTrack(track)
                }
            }

            // ── 미사용 콜백 ──
            override fun onSignalingChange(state: PeerConnection.SignalingState) {}
            override fun onIceConnectionReceivingChange(receiving: Boolean) {}
            override fun onIceGatheringChange(state: PeerConnection.IceGatheringState) {}
            override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>) {}
            override fun onAddStream(stream: MediaStream) {}
            override fun onRemoveStream(stream: MediaStream) {}
            override fun onDataChannel(channel: DataChannel) {}
            override fun onRenegotiationNeeded() {}
        })?.also { pc ->
            // 영상 수신 전용 트랜시버 추가
            pc.addTransceiver(
                MediaStreamTrack.MediaType.MEDIA_TYPE_VIDEO,
                RtpTransceiver.RtpTransceiverInit(RtpTransceiver.RtpTransceiverDirection.RECV_ONLY)
            )
        }
    }

    // ─── Offer 생성 ───────────────────────────────────────────────────────────

    fun createOffer(
        onSuccess: (SessionDescription) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "false"))
        }

        peerConnection?.createOffer(object : SdpObserver {
            override fun onCreateSuccess(sdp: SessionDescription) {
                peerConnection?.setLocalDescription(object : SdpObserver {
                    override fun onSetSuccess() {
                        Log.d(TAG, "Local description set (offer)")
                        onSuccess(sdp)
                    }

                    override fun onSetFailure(error: String?) {
                        onFailure(error ?: "setLocalDescription 실패")
                    }

                    override fun onCreateSuccess(p0: SessionDescription?) {}
                    override fun onCreateFailure(p0: String?) {}
                }, sdp)
            }

            override fun onCreateFailure(error: String?) {
                onFailure(error ?: "createOffer 실패")
            }

            override fun onSetSuccess() {}
            override fun onSetFailure(p0: String?) {}
        }, constraints)
    }

    // ─── Answer 수신 ─────────────────────────────────────────────────────────

    fun setRemoteDescription(
        sdp: SessionDescription,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        peerConnection?.setRemoteDescription(object : SdpObserver {
            override fun onSetSuccess() {
                Log.d(TAG, "Remote description set (answer)")
                onSuccess()
            }

            override fun onSetFailure(error: String?) {
                onFailure(error ?: "setRemoteDescription 실패")
            }

            override fun onCreateSuccess(p0: SessionDescription?) {}
            override fun onCreateFailure(p0: String?) {}
        }, sdp)
    }

    // ─── ICE candidate 추가 ───────────────────────────────────────────────────

    fun addIceCandidate(candidate: IceCandidate) {
        peerConnection?.addIceCandidate(candidate)
    }

    // ─── SurfaceViewRenderer 초기화 헬퍼 ─────────────────────────────────────

    fun initRenderer(renderer: SurfaceViewRenderer) {
        renderer.init(eglBase.eglBaseContext, null)
        renderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
        renderer.setMirror(false)
    }

    // ─── 리소스 해제 ─────────────────────────────────────────────────────────

    fun release() {
        peerConnection?.close()
        peerConnection = null
        factory?.dispose()
        factory = null
        eglBase.release()
        Log.d(TAG, "WebRtcClient released")
    }
}
