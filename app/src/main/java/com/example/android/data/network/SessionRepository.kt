package com.example.android.data.network

import android.util.Log

/**
 * 로그인/로그아웃 흐름을 담당합니다.
 *
 * 로그인 성공 시 서버가 Set-Cookie: JSESSIONID=... 헤더를 응답하며,
 * [PersistentCookieJar]가 이를 자동으로 저장합니다.
 * 이후 모든 API 요청에 JSESSIONID 쿠키가 자동으로 첨부됩니다.
 */
class SessionRepository {

    private val api get() = RetrofitClient.apiService

    private companion object {
        const val TAG = "FCM_TEST"
    }

    /**
     * 로그인 요청.
     * - email, password를 form-encoded로 전송 (Spring Security formLogin)
     * - 성공 시 JSESSIONID 쿠키 자동 저장 후 FCM 토큰 등록
     */
    suspend fun login(email: String, password: String): Result<SessionUser> = runCatching {
        Log.d(TAG, "로그인 요청 시작: email=$email")

        val response = api.login(email, password)

        Log.d(TAG, "로그인 응답 코드: ${response.code()}")

        if (!response.isSuccessful) {
            val errorMsg = when (response.code()) {
                401 -> "아이디 또는 비밀번호가 올바르지 않습니다."
                else -> "로그인 실패 (${response.code()})"
            }

            Log.e(TAG, "로그인 실패: $errorMsg")
            error(errorMsg)
        }

        val body = response.body()

        if (body?.success != true || body.data == null) {
            val message = body?.error?.message ?: "로그인 중 오류가 발생했습니다."
            Log.e(TAG, "로그인 응답 body 오류: $message")
            error(message)
        }

        Log.d(TAG, "로그인 성공: userId=${body.data.id}, name=${body.data.name}")

        // 사용자 표시 이름 저장 (닉네임 우선)
        SessionManager.displayName = body.data.nickname.ifBlank { body.data.name }

        // FCM 토큰 등록 (실패해도 로그인 자체는 성공 처리)
        registerFcmToken()

        body.data
    }

    /**
     * 로그아웃 요청.
     * - 네트워크 오류가 발생해도 로컬 쿠키는 항상 삭제합니다.
     */
    suspend fun logout(): Result<Unit> {
        Log.d(TAG, "로그아웃 요청 시작")

        runCatching {
            api.logout()
        }.onSuccess {
            Log.d(TAG, "서버 로그아웃 요청 완료")
        }.onFailure {
            Log.e(TAG, "서버 로그아웃 요청 실패 - 로컬 세션은 삭제함", it)
        }

        SessionManager.clearSession()
        Log.d(TAG, "로컬 세션 삭제 완료")

        return Result.success(Unit)
    }

    /**
     * 내 정보 조회.
     * - 세션이 유효한 경우에만 성공합니다.
     */
    suspend fun getMyInfo(): Result<MyInfo> = runCatching {
        Log.d(TAG, "내 정보 조회 요청 시작")

        val response = api.getMyInfo()

        Log.d(TAG, "내 정보 조회 응답 코드: ${response.code()}")

        if (!response.isSuccessful) {
            error("내 정보 조회 실패 (${response.code()})")
        }

        val body = response.body()

        if (body?.success != true || body.data == null) {
            error(body?.error?.message ?: "내 정보를 불러올 수 없습니다.")
        }

        Log.d(TAG, "내 정보 조회 성공: ${body.data}")

        body.data
    }

    // ─── 내부 ──────────────────────────────────────────────────────────────────

    private suspend fun registerFcmToken() {
        Log.d(TAG, "FCM 토큰 등록 시작")

        val token = FcmTokenProvider.getToken()
        if (token == null) {
            Log.e(TAG, "FCM 토큰이 null이라 서버 등록을 건너뜀")
            return
        }

        val deviceId = SessionManager.deviceId
        if (deviceId == null) {
            Log.e(TAG, "deviceId가 null이라 FCM 토큰 서버 등록을 건너뜀")
            return
        }

        Log.d(TAG, "FCM 토큰 서버 등록 요청")
        Log.d(TAG, "deviceId=$deviceId")
        Log.d(TAG, "fcmToken=$token")

        runCatching {
            api.registerFcmToken(
                FcmTokenRequest(
                    fcmToken = token,
                    deviceId = deviceId
                )
            )
        }.onSuccess { response ->
            Log.d(TAG, "FCM 토큰 서버 등록 응답 코드: ${response.code()}")

            if (response.isSuccessful) {
                Log.d(TAG, "FCM 토큰 서버 등록 성공")
                Log.d(TAG, "서버 응답 body: ${response.body()}")
            } else {
                Log.e(TAG, "FCM 토큰 서버 등록 실패: code=${response.code()}")
                Log.e(TAG, "errorBody=${response.errorBody()?.string()}")
            }
        }.onFailure { exception ->
            Log.e(TAG, "FCM 토큰 서버 등록 중 예외 발생", exception)
        }
    }

    /**
     * 앱 실행 시마다 호출: 세션이 살아 있는 상태에서 FCM 토큰을 갱신합니다.
     * 실패해도 앱 동작에는 영향 없음.
     */
    suspend fun refreshFcmToken() {
        Log.d(TAG, "FCM 토큰 갱신 요청")

        if (!SessionManager.hasValidSession) {
            Log.d(TAG, "유효한 세션이 없어 FCM 토큰 갱신 건너뜀")
            return
        }

        registerFcmToken()
    }
}