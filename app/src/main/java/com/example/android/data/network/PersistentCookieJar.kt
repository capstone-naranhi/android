package com.example.android.data.network

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * 쿠키를 SharedPreferences에 영속 저장하는 CookieJar.
 *
 * - 서버 응답의 Set-Cookie 헤더를 자동 저장합니다.
 * - 이후 모든 요청에 저장된 쿠키(JSESSIONID 포함)를 자동 첨부합니다.
 * - 앱을 재시작해도 쿠키가 유지됩니다.
 */
class PersistentCookieJar(context: Context) : CookieJar {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("okhttp_cookies", Context.MODE_PRIVATE)

    // ─── CookieJar 구현 ──────────────────────────────────────────────────────

    /** 서버 응답의 쿠키를 저장합니다. key = "host::name" */
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        prefs.edit().apply {
            cookies.forEach { cookie ->
                putString("${url.host}::${cookie.name}", cookie.value)
            }
            apply()
        }
    }

    /** 요청 URL의 host에 해당하는 저장된 쿠키를 반환합니다. */
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val host = url.host
        return prefs.all.entries
            .filter { (key, _) -> key.startsWith("$host::") }
            .map { (key, value) ->
                Cookie.Builder()
                    .name(key.removePrefix("$host::"))
                    .value(value as String)
                    .domain(host)
                    .path("/")
                    .build()
            }
    }

    // ─── 세션 확인 / 삭제 ────────────────────────────────────────────────────

    /** 지정 host의 JSESSIONID 쿠키가 존재하는지 확인합니다. */
    fun hasJSessionId(host: String): Boolean =
        prefs.contains("$host::JSESSIONID")

    /** JSESSIONID 값을 반환합니다. */
    fun getJSessionId(host: String): String? =
        prefs.getString("$host::JSESSIONID", null)

    /** 저장된 모든 쿠키를 삭제합니다 (로그아웃 시 사용). */
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
