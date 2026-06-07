package com.example.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.android.data.network.SessionManager
import com.example.android.ui.theme.AndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // SharedPreferences 기반 쿠키 저장소 초기화 (AppNavigation보다 먼저 호출해야 함)
        SessionManager.init(this)
        enableEdgeToEdge()
        setContent {
            AndroidTheme {
                AppNavigation()
            }
        }
    }
}
