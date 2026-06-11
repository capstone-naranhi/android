package com.example.android.data.model

/** POST /api/v1/live/session 요청 */
data class LiveSessionRequest(
    val deviceId: Long
)

/** POST /api/v1/live/session 응답 */
data class LiveSessionData(
    val sessionId: String,
    val deviceSerial: String
)
