package com.example.android.data.model

/** POST /api/v1/devices/register 요청 */
data class RegisterDeviceRequest(
    val deviceSerial: String,
    val deviceName: String,
    val locationName: String
)

/** POST /api/v1/devices/register 응답 */
data class RegisterDeviceData(
    val deviceId: Long,
    val deviceName: String,
    val deviceSerialNumber: String,
    val locationName: String
)
