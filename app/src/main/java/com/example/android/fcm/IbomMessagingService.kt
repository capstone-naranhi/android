package com.example.android.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.android.MainActivity
import com.example.android.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * FCM 수신 처리 서비스.
 *
 * 백엔드 FcmPayload.data 필드:
 *   - type    : "SAFETY" | "DEVICE"
 *   - notifId : 알림 ID (상세 화면 이동용)
 *   - screen  : "NOTIFICATION_DETAIL" | "DEVICE_DETAIL"
 *
 * 앱이 포그라운드 상태일 때도 알림을 직접 표시하기 위해
 * onMessageReceived 에서 항상 알림을 생성합니다.
 */
class IbomMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: message.data["title"] ?: return
        val body  = message.notification?.body  ?: message.data["body"]  ?: return

        val type    = message.data["type"]    ?: "SAFETY"
        val notifId = message.data["notifId"] ?: ""
        val screen  = message.data["screen"]  ?: "NOTIFICATION_DETAIL"

        // 앱 포그라운드 여부와 무관하게 LiveScreen 최근 활동에 즉시 반영
        FcmEventBus.emit(
            FcmEvent(
                title    = title,
                body     = body,
                type     = type,
                timeText = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
            )
        )

        showNotification(
            title   = title,
            body    = body,
            type    = type,
            notifId = notifId,
            screen  = screen
        )
    }

    /** FCM 토큰이 갱신될 때 호출 — 앱이 실행 중이 아닐 수 있으므로 저장만 해둠 */
    override fun onNewToken(token: String) {
        // 다음 앱 실행 시 MainActivity → refreshFcmToken() 에서 서버에 전달됨
    }

    // ─── 알림 표시 ────────────────────────────────────────────────────────────

    private fun showNotification(
        title: String,
        body: String,
        type: String,
        notifId: String,
        screen: String
    ) {
        val channelId = channelIdFor(type)
        ensureChannelExists(channelId, type)

        val tapIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_TYPE,     type)
            putExtra(EXTRA_NOTIF_ID, notifId)
            putExtra(EXTRA_SCREEN,   screen)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            notifId.hashCode(),
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val priority = if (type == TYPE_SAFETY)
            NotificationCompat.PRIORITY_HIGH
        else
            NotificationCompat.PRIORITY_DEFAULT

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(priority)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notifId.hashCode(), notification)
    }

    private fun channelIdFor(type: String) = when (type) {
        TYPE_SAFETY -> CHANNEL_SAFETY
        TYPE_DEVICE -> CHANNEL_DEVICE
        else        -> CHANNEL_GENERAL
    }

    private fun ensureChannelExists(channelId: String, type: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(channelId) != null) return

        val (name, description, importance) = when (type) {
            TYPE_SAFETY -> Triple(
                "안전 알림",
                "낙상, 질식, 울음 등 아이 안전 관련 알림",
                NotificationManager.IMPORTANCE_HIGH
            )
            TYPE_DEVICE -> Triple(
                "기기 알림",
                "카메라, 마이크 등 기기 연결 상태 알림",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            else -> Triple(
                "일반 알림",
                "공지 및 기타 알림",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        }

        val channel = NotificationChannel(channelId, name, importance).apply {
            this.description = description
        }
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val EXTRA_TYPE     = "fcm_type"
        const val EXTRA_NOTIF_ID = "fcm_notif_id"
        const val EXTRA_SCREEN   = "fcm_screen"

        const val TYPE_SAFETY  = "SAFETY"
        const val TYPE_DEVICE  = "DEVICE"

        private const val CHANNEL_SAFETY  = "ibom_safety"
        private const val CHANNEL_DEVICE  = "ibom_device"
        private const val CHANNEL_GENERAL = "ibom_general"
    }
}
