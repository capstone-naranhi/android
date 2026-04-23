package com.example.android.data.model

/** 장치 컴포넌트 상태 (온라인/오프라인) */
data class DeviceComponentStatus(
    val name: String,
    val isOnline: Boolean
)

/** 안전 알림 상세 데이터 */
data class SafetyNotificationDetail(
    val id: String,
    val category: NotificationCategory,
    val badgeLabel: String,
    val title: String,
    val cameraName: String,
    val dateTimeText: String,
    // 녹화 영상
    val hasRecording: Boolean = false,
    val recordingDurationText: String = "",
    // 감지 정보
    val eventType: String,
    val severity: String,           // e.g. "위험 (DANGER)"
    val severityLevel: NotificationSeverity,
    val durationText: String,       // e.g. "8초"
    val camera: String,
    // 감지 당시 장치 상태
    val deviceComponents: List<DeviceComponentStatus>
)

/** 장치 알림 상세 데이터 */
data class DeviceNotificationDetail(
    val id: String,
    val badgeLabel: String,
    val title: String,
    val deviceDescription: String,  // e.g. "거실 카메라"
    val dateTimeText: String,
    // 장치 변경 정보
    val component: String,          // e.g. "카메라"
    val previousStatus: String,     // "ONLINE"
    val currentStatus: String,      // "OFFLINE"
    val deviceName: String,         // "거실"
    val reason: String,             // "네트워크 연결 끊김"
    // 동일 장치 컴포넌트 현황
    val componentStatuses: List<DeviceComponentStatus>,
    val lastHeartbeat: String       // "09:14:02"
)
