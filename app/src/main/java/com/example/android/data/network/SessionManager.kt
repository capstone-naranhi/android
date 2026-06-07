package com.example.android.data.network

import android.content.Context

/**
 * 세션 상태를 전역으로 관리합니다.
 *
 * 세션 유효 여부는 로컬에 저장된 JSESSIONID 쿠키의 존재로 판단합니다.
 * 실제 쿠키 전송은 [PersistentCookieJar]가 OkHttp 레벨에서 자동 처리합니다.
 *
 * 사용 방법: Application 또는 MainActivity.onCreate 에서 [init] 호출 필수.
 */
object SessionManager {

    lateinit var cookieJar: PersistentCookieJar
        private set

    fun init(context: Context) {
        cookieJar = PersistentCookieJar(context.applicationContext)
    }

    /**
     * 로컬에 저장된 JSESSIONID 쿠키가 있으면 세션이 유효하다고 판단합니다.
     * 앱 시작 시 이 값으로 로그인 화면 노출 여부를 결정합니다.
     */
    val hasValidSession: Boolean
        get() = cookieJar.hasJSessionId(NetworkConfig.host)

    /** 로그아웃: 저장된 모든 쿠키를 삭제합니다. */
    fun clearSession() {
        cookieJar.clearAll()
    }
}
