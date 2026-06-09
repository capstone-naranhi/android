package com.example.android.data.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// ─── 날짜 포맷 유틸 ───────────────────────────────────────────────────────────

/** Any?(배열/문자열) → "2026. 04. 20 · 오전 10:32" 형식 */
fun Any?.toKoreanDateTimeString(): String {
    val str = toDateTimeString() ?: return ""
    return try {
        val inFmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = inFmt.parse(str) ?: return str
        val cal = Calendar.getInstance().apply { time = date }
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val amPm = if (hour < 12) "오전" else "오후"
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        val min = cal.get(Calendar.MINUTE)
        "%d. %02d. %02d · %s %d:%02d".format(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH),
            amPm, displayHour, min
        )
    } catch (e: Exception) {
        ""
    }
}

/** 초(Int) → "X초" / "X분 X초" */
fun Int?.toDurationText(): String {
    if (this == null || this <= 0) return "-"
    val min = this / 60
    val sec = this % 60
    return if (min > 0) "${min}분 ${sec}초" else "${sec}초"
}

/** 컴포넌트 타입 코드 → 한국어 */
fun String?.toComponentLabel(): String = when (this?.uppercase()) {
    "CAMERA" -> "카메라"
    "MIC" -> "마이크"
    "BOARD" -> "보드"
    else -> this ?: "-"
}

/** 상태 코드 → 한국어 */
fun String?.toStatusLabel(): String = when (this?.uppercase()) {
    "ONLINE" -> "온라인"
    "OFFLINE" -> "오프라인"
    "ERROR" -> "오류"
    else -> this ?: "-"
}

/** severity 문자열 → 한국어 배지 */
fun String?.toSeverityLabel(): String = when (this?.uppercase()) {
    "DANGER" -> "위험"
    "CAUTION" -> "주의"
    "INFO" -> "정보"
    else -> this ?: "-"
}

// ─── API 응답 모델 ─────────────────────────────────────────────────────────────

/** GET /api/v1/notifications/{notificationId} 응답 */
data class NotificationDetailData(
    val notificationId: Long,
    /** SAFETY / DEVICE / GENERAL */
    val type: String?,
    /** LocalDateTime → Any? (배열 또는 ISO 문자열) */
    val sentAt: Any?,
    val isRead: Boolean,
    val safetyDetail: NotifSafetyDetail?,
    val deviceDetail: NotifDeviceDetail?,
    val generalDetail: NotifGeneralDetail?
)

data class NotifSafetyDetail(
    val deviceId: Long?,
    val deviceName: String?,
    val eventType: NotifEventTypeResponse?,
    /** DANGER / CAUTION / INFO */
    val severity: String?,
    val durationSecond: Int?,
    val confidence: Double?,
    /** LocalDateTime → Any? */
    val detectedAt: Any?,
    val snapshotUrl: String?,
    val videoUrl: String?
)

data class NotifEventTypeResponse(
    val code: String?,
    /** 서버 EventType.description (한국어) ex. "자세 뒤집힘 질식 위험" */
    val label: String?
)

data class NotifDeviceDetail(
    val deviceId: Long?,
    val deviceName: String?,
    /** CAMERA / MIC / BOARD */
    val componentType: String?,
    /** ONLINE / OFFLINE */
    val beforeStatus: String?,
    val currentStatus: String?,
    /** 상태 변경 사유 */
    val description: String?
)

data class NotifGeneralDetail(
    val detailType: String?,
    val title: String?
)
