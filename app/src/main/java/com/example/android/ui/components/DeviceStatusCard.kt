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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.android.ui.theme.Dimens
import com.example.android.ui.theme.NeutralSubText
import com.example.android.ui.theme.NeutralText
import com.example.android.ui.theme.SeverityColors

/**
 * 장치 상태 항목 1개
 *
 * @param label      장치 이름 (예: "카메라", "배터리", "Wi-Fi")
 * @param statusText 상태 설명 (예: "연결됨", "85%", "강함")
 * @param statusType 시각 상태 타입
 * @param icon       장치 아이콘
 */
data class DeviceStatusItem(
    val label: String,
    val statusText: String,
    val statusType: StatusType,
    val icon: ImageVector
)

/**
 * 홈화면 "장치 상태" 섹션 카드
 *
 * 카메라·배터리·Wi-Fi 등 연결된 장치의 현재 상태를
 * 한눈에 볼 수 있도록 리스트 형태로 보여준다.
 *
 * @param devices  표시할 장치 상태 목록
 */
@Composable
fun DeviceStatusCard(
    devices: List<DeviceStatusItem>,
    modifier: Modifier = Modifier
) {
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
                DeviceStatusRow(item = device)
            }
        }
    }
}

@Composable
private fun DeviceStatusRow(item: DeviceStatusItem) {
    val colors = when (item.statusType) {
        StatusType.SUCCESS -> SeverityColors.success()
        StatusType.WARNING -> SeverityColors.warning()
        StatusType.DANGER  -> SeverityColors.danger()
        StatusType.INFO    -> SeverityColors.info()
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 아이콘 + 장치 이름
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

        // 상태 배지
        StatusBadge(text = item.statusText, colors = colors)
    }
}

@Composable
private fun StatusBadge(text: String, colors: SeverityColors) {
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

// ── 미리 만들어 둔 샘플 데이터 (HomeScreen Preview 용) ─────────────────────────

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
