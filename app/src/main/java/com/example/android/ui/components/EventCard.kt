package com.example.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.android.R
import com.example.android.data.model.EventIcon
import com.example.android.data.model.EventItem
import com.example.android.data.model.EventSeverity
import com.example.android.ui.theme.Dimens
import com.example.android.ui.theme.ReadBadge
import com.example.android.ui.theme.ReadBadgeText
import com.example.android.ui.theme.ReadContent
import com.example.android.ui.theme.SeverityColors

// ─── 배지 문자열 매핑 ──────────────────────────────────────────────────────────

private val EventSeverity.badgeTextResId: Int
    get() = when (this) {
        EventSeverity.DANGER -> R.string.severity_danger
        EventSeverity.WARNING -> R.string.severity_warning
        EventSeverity.INFO -> R.string.severity_info
    }

// ─── 컴포넌트 ─────────────────────────────────────────────────────────────────

@Composable
fun EventCard(
    event: EventItem,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val severity = event.severityOverride ?: event.eventType.defaultSeverity

    // 읽음 여부에 따라 색상 선택 — SeverityColors 로 단일화
    val colors = if (event.isRead) SeverityColors.read() else when (severity) {
        EventSeverity.DANGER -> SeverityColors.danger()
        EventSeverity.WARNING -> SeverityColors.warning()
        EventSeverity.INFO -> SeverityColors.info()
    }

    val badgeBackground = if (event.isRead) ReadBadge else colors.content
    val badgeText = if (event.isRead) ReadBadgeText else Color.White
    val arrowColor = if (event.isRead) ReadContent else colors.content

    val messageText = event.messageOverride ?: stringResource(event.eventType.descriptionResId)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(Dimens.radiusCard),
        colors = CardDefaults.cardColors(containerColor = colors.container),
        elevation = CardDefaults.cardElevation(defaultElevation = if (event.isRead) 2.dp else 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // 왼쪽: 아이콘 + 텍스트
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(Dimens.avatarM)
                        .background(
                            color = colors.iconBg,
                            shape = RoundedCornerShape(Dimens.radiusM)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    when (val icon = event.eventType.icon) {
                        is EventIcon.Vector -> Icon(
                            imageVector = icon.imageVector,
                            contentDescription = stringResource(R.string.event_icon_content_description),
                            tint = colors.content,
                            modifier = Modifier.size(Dimens.iconL)
                        )
                        is EventIcon.Resource -> Icon(
                            painter = painterResource(icon.resId),
                            contentDescription = stringResource(R.string.event_icon_content_description),
                            tint = colors.content,
                            modifier = Modifier.size(Dimens.iconL)
                        )
                    }
                }

                Spacer(modifier = Modifier.size(Dimens.spaceL))

                Column {
                    Text(
                        text = stringResource(event.eventType.titleResId),
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.content
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = messageText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.content
                    )
                    Spacer(modifier = Modifier.size(Dimens.spaceS))
                    Text(
                        text = event.timeText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.content.copy(alpha = 0.85f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(Dimens.spaceS))

            // 오른쪽: 심각도 배지 + 화살표
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceXs)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = badgeBackground,
                            shape = RoundedCornerShape(999.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(severity.badgeTextResId),
                        style = MaterialTheme.typography.labelLarge,
                        color = badgeText
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = stringResource(R.string.event_detail_arrow_content_description),
                    tint = arrowColor,
                    modifier = Modifier.size(Dimens.iconM)
                )
            }
        }
    }
}