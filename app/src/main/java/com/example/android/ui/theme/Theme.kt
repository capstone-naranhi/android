package com.example.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppColorScheme = darkColorScheme(
    primary          = Color(0xFF7BA7E0),
    primaryContainer = BrandPrimary,
    background       = DarkBg,
    surface          = DarkSurface,
    surfaceVariant   = DarkCard,
    onPrimary        = Color.White,
    onBackground     = DarkOnSurface,
    onSurface        = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceVar,
)

@Composable
fun AndroidTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography  = Typography,
        content     = content
    )
}