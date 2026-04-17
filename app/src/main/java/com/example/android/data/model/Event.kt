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

/** 이벤트 심각도 */
enum class EventSeverity { DANGER, WARNING, INFO }

/**
 * 이벤트 아이콘 출처.
 *
 * 대부분의 이벤트는 Material Icons Extended의 [ImageVector]를 사용하지만,
 * Extended에 존재하지 않는 아이콘(낙상 등)은 drawable 리소스를 유지한다.
 */
sealed interface EventIcon {
    data class Vector(val imageVector: ImageVector) : EventIcon
    data class Resource(@DrawableRes val resId: Int) : EventIcon
}

/**
 * 이벤트 종류
 *
 * 질식·낙상·등반·울음·칭얼거림·비명 감지
 */
enum class EventType(
    val titleResId: Int,
    val descriptionResId: Int,
    val defaultSeverity: EventSeverity,
    val icon: EventIcon
) {
    SUFFOCATION(
        R.string.event_suffocation_title,
        R.string.event_suffocation_description,
        EventSeverity.DANGER,
        EventIcon.Vector(Icons.Outlined.Air)
    ),
    FALL(
        R.string.event_fall_title,
        R.string.event_fall_description,
        EventSeverity.DANGER,
        EventIcon.Resource(R.drawable.ic_event_fall)
    ),
    CLIMBING(
        R.string.event_climbing_title,
        R.string.event_climbing_description,
        EventSeverity.DANGER,
        EventIcon.Vector(Icons.Outlined.Stairs)
    ),
    CRYING(
        R.string.event_crying_title,
        R.string.event_crying_description,
        EventSeverity.WARNING,
        EventIcon.Vector(Icons.Outlined.WaterDrop)
    ),
    FUSSING(
        R.string.event_fussing_title,
        R.string.event_fussing_description,
        EventSeverity.INFO,
        EventIcon.Vector(Icons.Outlined.RecordVoiceOver)
    ),
    SCREAM(
        R.string.event_scream_title,
        R.string.event_scream_description,
        EventSeverity.DANGER,
        EventIcon.Vector(Icons.Outlined.Campaign)
    ),
}

/** 실제 발생한 이벤트 1건 */
data class EventItem(
    val id: String,
    val eventType: EventType,
    val timeText: String,
    val isRead: Boolean,
    val messageOverride: String? = null,
    val severityOverride: EventSeverity? = null,
)
