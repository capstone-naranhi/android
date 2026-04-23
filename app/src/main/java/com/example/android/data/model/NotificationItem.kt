package com.example.android.data.model

import androidx.compose.ui.graphics.vector.ImageVector

enum class NotificationCategory {
    SAFETY_DANGER,
    SAFETY_CAUTION,
    SAFETY_INFO,
    DEVICE,
    REPORT,
    AD
}

data class NotificationItem(
    val id: String,
    val category: NotificationCategory,
    val icon: ImageVector,
    val badgeLabel: String,
    val description: String,
    val title: String = "",   // AD 카테고리에서만 사용
    val timeText: String,
    val isRead: Boolean
)
