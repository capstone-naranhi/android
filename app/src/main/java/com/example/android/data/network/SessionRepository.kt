package com.example.android.data.network

/**
 * 로그인/로그아웃 흐름을 담당합니다.
 *
 * 로그인 성공 시 서버가 Set-Cookie: JSESSIONID=... 헤더를 응답하며,
 * [PersistentCookieJar]가 이를 자동으로 저장합니다.
 * 이후 모든 API 요청에 JSESSIONID 쿠키가 자동으로 첨부됩니다.
 */
class SessionRepository {

    private val api get() = RetrofitClient.apiService

    /**
     * 로그인 요청.
     * - email, password를 form-encoded로 전송 (Spring Security formLogin)
     * - 성공 시 JSESSIONID 쿠키 자동 저장 후 FCM 토큰 등록
     */
    suspend fun login(email: String, password: String): Result<SessionUser> = runCatching {
        val response = api.login(email, password)
        if (!response.isSuccessful) {
            val errorMsg = when (response.code()) {
                401 -> "아이디 또는 비밀번호가 올바르지 않습니다."
                else -> "로그인 실패 (${response.code()})"
            }
            error(errorMsg)
        }
        val body = response.body()
        if (body?.success != true || body.data == null) {
            error(body?.error?.message ?: "로그인 중 오류가 발생했습니다.")
        }
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
        runCatching { api.logout() }   // 서버 세션 무효화 (실패 무시)
        SessionManager.clearSession() // 로컬 쿠키 삭제
        return Result.success(Unit)
    }

    /**
     * 내 정보 조회.
     * - 세션이 유효한 경우에만 성공합니다.
     */
    suspend fun getMyInfo(): Result<MyInfo> = runCatching {
        val response = api.getMyInfo()
        if (!response.isSuccessful) {
            error("내 정보 조회 실패 (${response.code()})")
        }
        val body = response.body()
        if (body?.success != true || body.data == null) {
            error(body?.error?.message ?: "내 정보를 불러올 수 없습니다.")
        }
        body.data
    }

    // ─── 내부 ──────────────────────────────────────────────────────────────────

    private suspend fun registerFcmToken() {
        val token = FcmTokenProvider.getToken() ?: return
        val deviceId = SessionManager.deviceId ?: return
        runCatching { api.registerFcmToken(FcmTokenRequest(fcmToken = token, deviceId = deviceId)) }
    }

    /**
     * 앱 실행 시마다 호출: 세션이 살아 있는 상태에서 FCM 토큰을 갱신합니다.
     * 실패해도 앱 동작에는 영향 없음.
     */
    suspend fun refreshFcmToken() {
        if (!SessionManager.hasValidSession) return
        registerFcmToken()
    }
}
