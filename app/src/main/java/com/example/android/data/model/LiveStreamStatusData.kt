package com.example.android.data.model

/** GET /api/v1/devices/{deviceId}/live-status 응답 */
data class LiveStreamStatusData(
    val deviceId: Long,
    val deviceName: String,
    /** ONLINE / OFFLINE */
    val boardStatus: String?,
    val cameraStatus: String?,
    val micStatus: String?,
    /** 마지막 하트비트로부터 60초 이내면 ONLINE */
    val heartbeatStatus: String?,
    /** durationSecond == 0 인 진행 중인 안전 이벤트, 없으면 null */
    val ongoingSafetyEvent: OngoingSafetyEventData?
)

data class OngoingSafetyEventData(
    val safetyEventId: Long,
    /** 서버 EventType enum 이름 (e.g. "SUFFOCATION", "FALL_RISK") */
    val eventType: String?,
    /** DANGER / CAUTION / INFO */
    val severity: String?,
    val detectedAt: Any?
)
