package com.example.android.ui.theme

import androidx.compose.ui.graphics.Color

/** 상태 종류 */
enum class StatusType { SUCCESS, WARNING, DANGER, INFO }

/**
 * DANGER / WARNING / INFO / SUCCESS 각 심각도에 대응하는 색상 묶음.
 *
 * @param container  카드 배경색
 * @param content    텍스트·아이콘 강조색
 * @param iconBg     아이콘 박스 배경색
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

        /** [StatusType]으로부터 색상 묶음을 반환한다. */
        fun of(statusType: StatusType) = when (statusType) {
            StatusType.SUCCESS -> success()
            StatusType.WARNING -> warning()
            StatusType.DANGER -> danger()
            StatusType.INFO -> info()
        }
    }
}
