package com.example.android.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * DANGER / WARNING / INFO / SUCCESS 각 심각도에 대응하는 색상 묶음.
 *
 * ### 만든 이유
 * `EventCard`와 `StatusCard` 양쪽에 거의 동일한 `when` 분기가 중복으로 존재했음.
 * 색상을 한 곳에서 관리하고, 각 컴포넌트는 `SeverityColors.of(severity)` 만 호출.
 *
 * @param container   카드 배경색
 * @param content     텍스트·아이콘 강조색
 * @param iconBg      아이콘 박스 배경색
 */
data class SeverityColors(
    val container: Color,
    val content: Color,
    val iconBg: Color,
) {
    companion object {
        fun danger() = SeverityColors(DangerContainer, DangerContent, DangerIconBg)
        fun warning() = SeverityColors(WarningContainer, WarningContent, WarningIconBg)
        fun info() = SeverityColors(InfoContainer, InfoContent, InfoIconBg)
        fun success() = SeverityColors(SuccessContainer, SuccessContent, SuccessIconBg)
        fun read() = SeverityColors(ReadContainer, ReadContent, ReadIconBg)
    }
}