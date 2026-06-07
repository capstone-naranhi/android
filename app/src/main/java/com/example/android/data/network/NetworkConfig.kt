package com.example.android.data.network

import com.example.android.BuildConfig

/**
 * 서버 접속 정보를 전역으로 관리합니다.
 *
 * IP/Port는 local.properties → BuildConfig 경로로 주입되므로
 * 소스코드 및 버전 관리에 노출되지 않습니다.
 *
 * local.properties 예시:
 *   SERVER_IP=192.168.0.10
 *   SERVER_PORT=8080
 */
object NetworkConfig {
    val serverIp: String = BuildConfig.SERVER_IP
    val serverPort: String = BuildConfig.SERVER_PORT

    val baseUrl: String = "http://$serverIp:$serverPort/"

    /** OkHttp 쿠키 매칭에 사용할 호스트 */
    val host: String = serverIp
}
