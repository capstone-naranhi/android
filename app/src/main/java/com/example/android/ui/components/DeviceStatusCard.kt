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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android.ui.theme.DarkOnSurface
import com.example.android.ui.theme.DarkSurface
import com.example.android.ui.theme.Dimens
import com.example.android.ui.theme.NanumSquareRound
import com.example.android.ui.theme.NeutralText
import com.example.android.ui.theme.SeverityColors
import com.example.android.ui.theme.StatusType

/**
 * 장치 상태 항목 1개
 *
 * @param label      장치 이름 (예: "보드", "카메라", "마이크")
 * @param statusText 상태 설명 (예: "연결됨", "연결 안됨")
 * @param statusType 상태 시각 타입
 * @param icon       장치 아이콘
 */
data class DeviceStatusItem(
    val label: String,
    val statusText: String,
    val statusType: StatusType,
    val icon: ImageVector
)

/**
 * 장치 상태 카드
 *
 * - [isDarkTheme] = false(기본): 라이트 테마 — 하나의 Card 안에 모든 장치 나열
 * - [isDarkTheme] = true: 다크 테마 — 홈화면 알림 카드와 동일한 스타일로 장치별 개별 행 렌더링
 */
@Composable
fun DeviceStatusCard(
    devices: List<DeviceStatusItem>,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = false
) {
    if (isDarkTheme) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            devices.forEach { device ->
                DeviceStatusRowDark(item = device)
            }
        }
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimens.radiusCard),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(Dimens.spaceXl),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceM)
            ) {
                devices.forEach { device ->
                    DeviceStatusRowLight(item = device)
                }
            }
        }
    }
}

// ─── 다크 테마 행 (홈화면 알림 카드와 동일한 스타일) ──────────────────────────

@Composable
private fun DeviceStatusRowDark(item: DeviceStatusItem) {
    val colors = SeverityColors.of(item.statusType)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(colors.iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = colors.content,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = item.label,
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = DarkOnSurface,
            modifier = Modifier.weight(1f)
        )

        DeviceStatusBadge(text = item.statusText, colors = colors)
    }
}

// ─── 라이트 테마 행 ────────────────────────────────────────────────────────────

@Composable
private fun DeviceStatusRowLight(item: DeviceStatusItem) {
    val colors = SeverityColors.of(item.statusType)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(color = colors.iconBg, shape = RoundedCornerShape(Dimens.radiusM)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = colors.content,
                    modifier = Modifier.size(Dimens.iconM)
                )
            }

            Spacer(modifier = Modifier.width(Dimens.spaceL))

            Text(
                text = item.label,
                style = MaterialTheme.typography.bodyLarge,
                color = NeutralText
            )
        }

        DeviceStatusBadge(text = item.statusText, colors = colors)
    }
}

// ─── 공통 상태 배지 ────────────────────────────────────────────────────────────

@Composable
private fun DeviceStatusBadge(text: String, colors: SeverityColors) {
    Box(
        modifier = Modifier
            .background(color = colors.container, shape = RoundedCornerShape(Dimens.radiusS))
            .padding(horizontal = Dimens.spaceL, vertical = Dimens.spaceS),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = colors.content
        )
    }
}

// ── 샘플 데이터 (Preview 전용) ────────────────────────────────────────────────

fun sampleDeviceStatusItems() = listOf(
    DeviceStatusItem(
        label = "Jetson Nano",
        statusText = "연결됨",
        statusType = StatusType.SUCCESS,
        icon = Icons.Outlined.Memory
    ),
    DeviceStatusItem(
        label = "카메라",
        statusText = "연결됨",
        statusType = StatusType.SUCCESS,
        icon = Icons.Outlined.Videocam
    ),
    DeviceStatusItem(
        label = "마이크",
        statusText = "연결됨",
        statusType = StatusType.SUCCESS,
        icon = Icons.Outlined.Mic
    )
)
