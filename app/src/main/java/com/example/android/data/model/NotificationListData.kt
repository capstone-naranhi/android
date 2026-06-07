package com.example.android.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.Stairs
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector

// ─── API 응답 모델 ─────────────────────────────────────────────────────────────

data class NotificationListData(
    val notifications: List<NotificationListItem>,
    val nextCursorId: Long?,
    val hasNext: Boolean
)

data class NotificationListItem(
    val notificationId: Long,
    val type: String,
    val sentAt: Any?,
    val isRead: Boolean,
    val safetyDetail: NotifListSafetyDetail?,
    val deviceDetail: NotifListDeviceDetail?,
    val generalDetail: NotifListGeneralDetail?
)

data class NotifListSafetyDetail(
    val deviceName: String?,
    val eventType: NotifListEventType?,
    val severity: String?,
    val durationSecond: Int?
)

data class NotifListEventType(
    val code: String?,
    val label: String?
)

data class NotifListDeviceDetail(
    val deviceId: Long?,
    val deviceName: String?,
    val componentType: String?,
    val beforeStatus: String?,
    val currentStatus: String?,
    val description: String?
)

data class NotifListGeneralDetail(
    val title: String?,
    val content: String?
)

data class UnreadCountData(
    val unreadCount: Int
)

// ─── UI 모델 변환 ──────────────────────────────────────────────────────────────

fun NotificationListItem.toNotificationItem(): NotificationItem {
    val category = when {
        type.equals("SAFETY", ignoreCase = true) -> when (safetyDetail?.severity?.uppercase()) {
            "DANGER"  -> NotificationCategory.SAFETY_DANGER
            "CAUTION" -> NotificationCategory.SAFETY_CAUTION
            else      -> NotificationCategory.SAFETY_INFO
        }
        type.equals("DEVICE", ignoreCase = true) -> NotificationCategory.DEVICE
        else -> NotificationCategory.REPORT
    }

    val icon: ImageVector = when {
        type.equals("SAFETY", ignoreCase = true) -> when (safetyDetail?.eventType?.code?.uppercase()) {
            "SUFFOCATION" -> Icons.Outlined.Air
            "CRYING"      -> Icons.Outlined.WaterDrop
            "FUSSING"     -> Icons.Outlined.RecordVoiceOver
            "SCREAM"      -> Icons.Outlined.Campaign
            "CLIMBING"    -> Icons.Outlined.Stairs
            else          -> Icons.Outlined.Warning
        }
        type.equals("DEVICE", ignoreCase = true) -> Icons.Outlined.Videocam
        else -> Icons.Outlined.Assessment
    }

    val badgeLabel = when {
        type.equals("SAFETY", ignoreCase = true) -> safetyDetail?.severity.toSeverityLabel()
        type.equals("DEVICE", ignoreCase = true) -> "장치"
        else -> "일반"
    }

    val description = when {
        type.equals("SAFETY", ignoreCase = true) -> {
            val name  = safetyDetail?.deviceName ?: "-"
            val event = safetyDetail?.eventType?.label ?: "안전 이벤트"
            "$name · $event"
        }
        type.equals("DEVICE", ignoreCase = true) -> {
            val name = deviceDetail?.deviceName ?: "-"
            val comp = deviceDetail?.componentType.toComponentLabel()
            "$name · $comp 상태 변경"
        }
        else -> generalDetail?.content ?: generalDetail?.title ?: "일반 알림"
    }

    return NotificationItem(
        id          = notificationId.toString(),
        category    = category,
        icon        = icon,
        badgeLabel  = badgeLabel,
        description = description,
        timeText    = sentAt.toTimeAgoText(),
        isRead      = isRead
    )
}
