package com.example.android.data.model

// ─── 서버 LocalDateTime 처리 유틸 ────────────────────────────────────────────────
// Spring Boot 기본 설정: LocalDateTime → JSON 배열 [y,m,d,h,min,s,ns]
// write-dates-as-timestamps=false 설정 시: ISO 문자열 "yyyy-MM-ddTHH:mm:ss"
// Gson이 Any?로 역직렬화할 경우: 배열 → ArrayList<Double>, 문자열 → String

import java.text.SimpleDateFormat
import java.util.Locale

fun Any?.toDateTimeString(): String? = when (this) {
    is String -> this
    is List<*> -> {
        val p = map { (it as? Double)?.toInt() ?: 0 }
        "%04d-%02d-%02dT%02d:%02d:%02d".format(
            p.getOrElse(0) { 2024 }, p.getOrElse(1) { 1 }, p.getOrElse(2) { 1 },
            p.getOrElse(3) { 0 }, p.getOrElse(4) { 0 }, p.getOrElse(5) { 0 }
        )
    }

    else -> null
}

fun Any?.toTimeAgoText(): String {
    val str = toDateTimeString() ?: return "방금"
    return try {
        val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = fmt.parse(str) ?: return "방금"
        val diffMin = (System.currentTimeMillis() - date.time) / 60_000
        when {
            diffMin < 1 -> "방금"
            diffMin < 60 -> "${diffMin}분 전"
            diffMin < 1440 -> "${diffMin / 60}시간 전"
            else -> "${diffMin / 1440}일 전"
        }
    } catch (e: Exception) {
        "방금"
    }
}

// ─── Enum ─────────────────────────────────────────────────────────────────────

/** 서버 ChildStatus enum (HomeResponse.java) */
enum class ChildStatus { SAFE, DANGER, CAUTION, INFO }

/** 서버 DeviceStatus enum (ONLINE/OFFLINE) */
enum class DeviceOnlineStatus { ONLINE, OFFLINE }

// ─── Home API 응답 최상위 ──────────────────────────────────────────────────────

data class HomeData(
    val currentStatus: HomeCurrentStatus,
    val todaySummary: HomeTodaySummary,
    val devices: List<HomeDevicePreview>,
    val recentNotifications: List<HomeNotificationItem>,
    val deviceStatuses: List<HomeDeviceStatusPreview>
)

// ─── CurrentStatus ────────────────────────────────────────────────────────────

data class HomeCurrentStatus(
    /** Spring Boot가 LocalDateTime 배열 또는 ISO 문자열로 반환 → Any? 로 처리 */
    val evaluatedAt: Any?,
    val childStatus: ChildStatus?
)

// ─── TodaySummary ─────────────────────────────────────────────────────────────

data class HomeTodaySummary(
    val todayNotificationCount: Long,
    val todayCryingCount: Long
)

// ─── DevicePreview ────────────────────────────────────────────────────────────

data class HomeDevicePreview(
    val deviceId: Long,
    val deviceName: String
)

// ─── DeviceStatusPreview ──────────────────────────────────────────────────────

data class HomeDeviceStatusPreview(
    val deviceId: Long,
    val deviceName: String,
    val boardStatus: DeviceOnlineStatus?,
    val cameraStatus: DeviceOnlineStatus?,
    val micStatus: DeviceOnlineStatus?
)

// ─── NotificationItem ─────────────────────────────────────────────────────────

data class HomeNotificationItem(
    val notificationId: Long,
    /** SAFETY / DEVICE / GENERAL */
    val type: String?,
    /** LocalDateTime → Any? (배열 또는 문자열) */
    val sentAt: Any?,
    val isRead: Boolean,
    val safetyDetail: HomeSafetyDetail?,
    val deviceDetail: HomeDeviceDetail?,
    val generalDetail: HomeGeneralDetail?
)

data class HomeSafetyDetail(
    val deviceName: String?,
    val eventType: HomeEventTypeResponse?,
    /** DANGER / CAUTION / INFO */
    val severity: String?,
    val durationSecond: Int?
)

data class HomeEventTypeResponse(
    val code: String?,
    val label: String?
)

data class HomeDeviceDetail(
    val deviceId: Long?,
    val deviceName: String?,
    val componentType: String?,
    val beforeStatus: String?,
    val currentStatus: String?,
    val description: String?
)

data class HomeGeneralDetail(
    val detailType: String?,
    val title: String?
)
