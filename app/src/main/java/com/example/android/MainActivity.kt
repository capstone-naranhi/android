package com.example.android

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.android.data.network.SessionManager
import com.example.android.data.network.SessionRepository
import com.example.android.fcm.IbomMessagingService.Companion.EXTRA_NOTIF_ID
import com.example.android.fcm.IbomMessagingService.Companion.EXTRA_SCREEN
import com.example.android.fcm.IbomMessagingService.Companion.EXTRA_TYPE
import com.example.android.ui.theme.AndroidTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val sessionRepository = SessionRepository()

    /** FCM 알림 탭 시 전달받은 딥링크 정보 (AppNavigation에서 소비) */
    var pendingFcmIntent: Intent? = null
        private set

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* 허용 여부와 무관하게 앱은 정상 동작 */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SessionManager.init(this)
        enableEdgeToEdge()

        // 알림 탭으로 실행된 경우 인텐트 저장
        pendingFcmIntent = intent.takeIf { it.hasExtra(EXTRA_TYPE) }

        setContent {
            AndroidTheme {
                AppNavigation()
            }
        }

        // Android 13+ 알림 권한 요청
        requestNotificationPermissionIfNeeded()

        // 앱 실행 시마다 FCM 토큰 갱신
        lifecycleScope.launch {
            sessionRepository.refreshFcmToken()
        }
    }

    /** 알림 탭으로 앱이 이미 실행 중일 때 (singleTop) */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasExtra(EXTRA_TYPE)) {
            pendingFcmIntent = intent
        }
    }

    /** 인텐트를 소비 — AppNavigation에서 딥링크 처리 후 호출 */
    fun consumeFcmIntent() {
        pendingFcmIntent = null
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
