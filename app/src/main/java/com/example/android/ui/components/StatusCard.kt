package com.example.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.android.ui.theme.Dimens
import com.example.android.ui.theme.SeverityColors

/** 상태 종류 */
enum class StatusType { SUCCESS, WARNING, DANGER, INFO }

/**
 * 현재 상태 카드
 *
 * @param title      상태의 핵심 제목
 * @param subtitle   상태에 대한 보조 설명
 * @param statusType 상태 시각 타입
 */
@Composable
fun StatusCard(
    title: String,
    subtitle: String,
    statusType: StatusType,
    modifier: Modifier = Modifier
) {
    // Triple 대신 SeverityColors — 필드 이름이 명확해서 실수 없음
    val colors = when (statusType) {
        StatusType.SUCCESS -> SeverityColors.success()
        StatusType.WARNING -> SeverityColors.warning()
        StatusType.DANGER -> SeverityColors.danger()
        StatusType.INFO -> SeverityColors.info()
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.radiusXl),
        colors = CardDefaults.cardColors(containerColor = colors.container),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spaceXl, vertical = 22.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(Dimens.avatarL)
                        .background(
                            color = colors.iconBg,
                            shape = RoundedCornerShape(Dimens.radiusL)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = "상태 아이콘",
                        tint = colors.content,
                        modifier = Modifier.size(Dimens.iconXl)
                    )
                }

                Spacer(modifier = Modifier.size(Dimens.spaceL))

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.content
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.content.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}