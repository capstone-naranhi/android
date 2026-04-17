package com.example.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.example.android.ui.screens.HomeScreen
import com.example.android.ui.theme.AndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 시스템 바 영역까지 앱이 자연스럽게 그려지도록 설정
        enableEdgeToEdge()
        setContent {
            AndroidTheme {
                HomeScreen()
            }
        }
    }
}