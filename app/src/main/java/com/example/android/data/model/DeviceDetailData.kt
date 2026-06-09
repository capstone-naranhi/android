package com.example.android.data.model

/** GET /api/v1/devices/{deviceId} 응답 */
data class DeviceDetailData(
    val deviceId: Long,
    val deviceName: String,
    val locationName: String,
    val deviceSerialNumber: String,
    /** ONLINE / OFFLINE */
    val boardStatus: String?,
    val cameraStatus: String?,
    val micStatus: String?,
    /** 마지막 하트비트로부터 60초 이내면 ONLINE */
    val heartbeatStatus: String?,
    val statusChangeLogs: List<StatusChangeLogData>
)

data class StatusChangeLogData(
    /** CAMERA / MIC / BOARD */
    val componentType: String?,
    /** ONLINE / OFFLINE */
    val beforeStatus: String?,
    val currentStatus: String?,
    /** LocalDateTime → Any? (배열 또는 ISO 문자열) */
    val changedAt: Any?
)
