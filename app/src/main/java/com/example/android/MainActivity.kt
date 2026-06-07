package com.example.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.android.data.network.SessionManager
import com.example.android.data.network.SessionRepository
import com.example.android.ui.theme.AndroidTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val sessionRepository = SessionRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // SharedPreferences 기반 쿠키 저장소 + deviceId 초기화
        SessionManager.init(this)
        enableEdgeToEdge()
        setContent {
            AndroidTheme {
                AppNavigation()
            }
        }
        // 앱 실행 시마다 FCM 토큰 갱신 (세션 있을 때만 실행, 실패 무시)
        lifecycleScope.launch {
            sessionRepository.refreshFcmToken()
        }
    }
}
