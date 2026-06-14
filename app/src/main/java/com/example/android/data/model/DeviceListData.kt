package com.example.android.data.model

/** GET /api/v1/devices 응답 */
data class DeviceListData(
    val devices: List<DeviceListItemData>
)

data class DeviceListItemData(
    val deviceId: Long,
    val deviceName: String,
    val locationName: String,
    /** ONLINE / OFFLINE */
    val boardStatus: String?,
    val cameraStatus: String?,
    val micStatus: String?,
    /** 마지막 하트비트로부터 60초 이내면 ONLINE */
    val heartbeatStatus: String?
)
