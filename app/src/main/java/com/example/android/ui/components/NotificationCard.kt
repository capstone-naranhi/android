package com.example.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android.data.model.NotificationCategory
import com.example.android.data.model.NotificationItem
import com.example.android.ui.theme.DangerContent
import com.example.android.ui.theme.InfoContent
import com.example.android.ui.theme.NanumSquareRound
import com.example.android.ui.theme.NeutralSubText
import com.example.android.ui.theme.NeutralText
import com.example.android.ui.theme.WarningContent

// ─── 카테고리별 스타일 ──────────────────────────────────────────────────────────

private data class CardStyle(
    val accentColor: Color,
    val panelBg: Color,
    val iconBg: Color
)

private fun styleFor(category: NotificationCategory): CardStyle = when (category) {
    NotificationCategory.SAFETY_DANGER -> CardStyle(
        accentColor = DangerContent,
        panelBg     = DangerContent.copy(alpha = 0.10f),
        iconBg      = DangerContent.copy(alpha = 0.18f)
    )
    NotificationCategory.SAFETY_CAUTION -> CardStyle(
        accentColor = WarningContent,
        panelBg     = WarningContent.copy(alpha = 0.10f),
        iconBg      = WarningContent.copy(alpha = 0.18f)
    )
    NotificationCategory.SAFETY_INFO -> CardStyle(
        accentColor = InfoContent,
        panelBg     = InfoContent.copy(alpha = 0.10f),
        iconBg      = InfoContent.copy(alpha = 0.18f)
    )
    NotificationCategory.DEVICE -> CardStyle(
        accentColor = Color(0xFF8B96AA),
        panelBg     = Color(0xFF8B96AA).copy(alpha = 0.10f),
        iconBg      = Color(0xFF8B96AA).copy(alpha = 0.18f)
    )
    NotificationCategory.REPORT -> CardStyle(
        accentColor = InfoContent,
        panelBg     = InfoContent.copy(alpha = 0.10f),
        iconBg      = InfoContent.copy(alpha = 0.18f)
    )
    NotificationCategory.AD -> CardStyle(
        accentColor = Color(0xFF667085),
        panelBg     = Color(0xFF667085).copy(alpha = 0.10f),
        iconBg      = Color(0xFF667085).copy(alpha = 0.18f)
    )
}

// ─── 컴포넌트 ─────────────────────────────────────────────────────────────────

@Composable
fun NotificationCard(
    notification: NotificationItem,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val base = styleFor(notification.category)
    val dimAlpha = if (notification.isRead) 0.45f else 1f

    val accentColor = base.accentColor.copy(alpha = dimAlpha)
    val panelBg     = base.panelBg.copy(alpha = if (notification.isRead) 0.06f else base.panelBg.alpha)
    val iconBg      = base.iconBg.copy(alpha = if (notification.isRead) 0.10f else base.iconBg.alpha)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onClick() }
    ) {
        // ── 좌측 컬러 패널 (얇은 바 + 아이콘 포함) ──────────────────────────
        Box(
            modifier = Modifier
                .width(72.dp)
                .fillMaxHeight()
                .background(panelBg)
        ) {
            // 얇은 강조 바 (좌측 끝)
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(accentColor)
                    .align(Alignment.CenterStart)
            )
            // 아이콘 박스 (패널 중앙)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = notification.icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // ── 우측 콘텐츠 ──────────────────────────────────────────────────────
        if (notification.category == NotificationCategory.AD) {
            AdContent(notification, accentColor, modifier = Modifier.weight(1f))
        } else {
            StandardContent(notification, accentColor, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun StandardContent(
    notification: NotificationItem,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 14.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NotificationBadge(label = notification.badgeLabel, accentColor = accentColor)
            Text(
                text = notification.timeText,
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                color = NeutralSubText
            )
        }
        Text(
            text = notification.description,
            fontFamily = NanumSquareRound,
            fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold,
            fontSize = 13.sp,
            color = if (notification.isRead) NeutralSubText else NeutralText
        )
    }
}

@Composable
private fun AdContent(
    notification: NotificationItem,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 14.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NotificationBadge(label = notification.badgeLabel, accentColor = accentColor)
            Text(
                text = notification.timeText,
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                color = NeutralSubText
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = notification.title,
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = NeutralText
        )
        Text(
            text = notification.description,
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = NeutralSubText
        )
    }
}

@Composable
private fun NotificationBadge(label: String, accentColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(accentColor.copy(alpha = 0.15f))
            .padding(horizontal = 9.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = accentColor
        )
    }
}
