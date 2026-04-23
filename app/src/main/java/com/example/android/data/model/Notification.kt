package com.example.android.data.model

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.Stairs
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.android.R

/** 알림 심각도 */
enum class NotificationSeverity { DANGER, WARNING, INFO }

/**
 * 알림 아이콘 출처.
 *
 * 대부분의 알림은 Material Icons Extended의 [ImageVector]를 사용하지만,
 * Extended에 존재하지 않는 아이콘(낙상 등)은 drawable 리소스를 유지한다.
 */
sealed interface NotificationIcon {
    data class Vector(val imageVector: ImageVector) : NotificationIcon
    data class Resource(@DrawableRes val resId: Int) : NotificationIcon
}

/**
 * 알림 종류
 *
 * 질식·낙상·등반·울음·칭얼거림·비명 감지
 */
enum class NotificationType(
    val titleResId: Int,
    val descriptionResId: Int,
    val defaultSeverity: NotificationSeverity,
    val icon: NotificationIcon
) {
    SUFFOCATION(
        R.string.notification_suffocation_title,
        R.string.notification_suffocation_description,
        NotificationSeverity.DANGER,
        NotificationIcon.Vector(Icons.Outlined.Air)
    ),
    FALL(
        R.string.notification_fall_title,
        R.string.notification_fall_description,
        NotificationSeverity.DANGER,
        NotificationIcon.Resource(R.drawable.ic_notification_fall)
    ),
    CLIMBING(
        R.string.notification_climbing_title,
        R.string.notification_climbing_description,
        NotificationSeverity.DANGER,
        NotificationIcon.Vector(Icons.Outlined.Stairs)
    ),
    CRYING(
        R.string.notification_crying_title,
        R.string.notification_crying_description,
        NotificationSeverity.WARNING,
        NotificationIcon.Vector(Icons.Outlined.WaterDrop)
    ),
    FUSSING(
        R.string.notification_fussing_title,
        R.string.notification_fussing_description,
        NotificationSeverity.INFO,
        NotificationIcon.Vector(Icons.Outlined.RecordVoiceOver)
    ),
    SCREAM(
        R.string.notification_scream_title,
        R.string.notification_scream_description,
        NotificationSeverity.DANGER,
        NotificationIcon.Vector(Icons.Outlined.Campaign)
    ),
}

/** 실제 발생한 알림 1건 */
data class NotificationEvent(
    val id: String,
    val notificationType: NotificationType,
    val timeText: String,
    val isRead: Boolean,
    val messageOverride: String? = null,
    val severityOverride: NotificationSeverity? = null,
)
