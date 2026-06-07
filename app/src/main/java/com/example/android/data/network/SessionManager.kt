package com.example.android.data.network

import android.content.Context
import android.provider.Settings

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

    /** ANDROID_ID — 기기 고유 식별자 (재설치해도 유지, 초기화 시 변경) */
    var deviceId: String? = null
        private set

    fun init(context: Context) {
        cookieJar = PersistentCookieJar(context.applicationContext)
        deviceId = Settings.Secure.getString(
            context.applicationContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    /**
     * 로컬에 저장된 JSESSIONID 쿠키가 있으면 세션이 유효하다고 판단합니다.
     * 앱 시작 시 이 값으로 로그인 화면 노출 여부를 결정합니다.
     */
    val hasValidSession: Boolean
        get() = cookieJar.hasJSessionId(NetworkConfig.host)

    /** 로그인 성공 후 저장되는 사용자 표시 이름 (닉네임 우선, 없으면 이름) */
    var displayName: String? = null

    /** 로그아웃: 저장된 모든 쿠키와 사용자 정보를 삭제합니다. */
    fun clearSession() {
        cookieJar.clearAll()
        displayName = null
    }
}
