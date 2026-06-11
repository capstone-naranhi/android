package com.example.android.data.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit 인스턴스를 생성합니다.
 *
 * - [PersistentCookieJar]를 통해 JSESSIONID를 비롯한 모든 쿠키가
 *   자동으로 저장되고 모든 요청에 첨부됩니다.
 * - [SessionManager.init]이 먼저 호출된 후 사용해야 합니다.
 * - Logcat 태그 "API" 로 요청/응답 전체(헤더·바디)를 출력합니다.
 */
object RetrofitClient {

    private const val LOG_TAG = "API"

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor { message ->
            Log.d(LOG_TAG, message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .cookieJar(SessionManager.cookieJar)   // 쿠키 자동 저장 & 전송
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(NetworkConfig.baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
