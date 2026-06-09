package com.example.android.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBackIos
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.android.data.model.DeviceDetailData
import com.example.android.data.model.StatusChangeLogData
import com.example.android.data.model.toComponentLabel
import com.example.android.data.model.toTimeAgoText
import com.example.android.ui.components.BottomNavigationBar
import com.example.android.ui.components.BottomNavigationItemType
import com.example.android.ui.theme.AndroidTheme
import com.example.android.ui.theme.AppBackground
import com.example.android.ui.theme.BrandPrimary
import com.example.android.ui.theme.DangerCardAccent
import com.example.android.ui.theme.DangerCardBg
import com.example.android.ui.theme.DangerChipBg
import com.example.android.ui.theme.DangerContent
import com.example.android.ui.theme.InfoContent
import com.example.android.ui.theme.NanumSquareRound
import com.example.android.ui.theme.NeutralSubText
import com.example.android.ui.theme.NeutralText
import com.example.android.ui.theme.SafeCardAccent
import com.example.android.ui.theme.SafeCardBg
import com.example.android.ui.theme.SafeChipBg
import com.example.android.ui.theme.StatusOnline

// ─── Data Models ──────────────────────────────────────────────────────────────

/**
 * 개별 컴포넌트 상태
 * @param isOnline null = 데이터 없음(알 수 없음)
 */
data class ComponentInfo(
    val label: String,
    val icon: ImageVector,
    val isOnline: Boolean?
)

/** 상태 변경 이력 항목 */
data class StatusHistoryEntry(
    val dotColor: Color,
    val title: String,
    val description: String,
    val timeText: String
)

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun DeviceDetailScreen(
    deviceId: Long,
    onBack: () -> Unit = {},
    viewModel: DeviceDetailViewModel = viewModel(
        key = "device_$deviceId",
        factory = DeviceDetailViewModel.factory(deviceId)
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = AppBackground,
        bottomBar = {
            BottomNavigationBar(
                selectedItem = BottomNavigationItemType.HOME,
                onItemSelected = {}
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            Spacer(Modifier.statusBarsPadding())

            val deviceName = when (val s = uiState) {
                is DeviceDetailUiState.Success -> s.data.deviceName
                else -> ""
            }

            DeviceDetailTopBar(deviceName = deviceName, onBack = onBack)
            HorizontalDivider(color = Color(0xFFF0F2F5))

            when (val state = uiState) {
                is DeviceDetailUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BrandPrimary)
                    }
                }

                is DeviceDetailUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            fontFamily = NanumSquareRound,
                            fontSize = 14.sp,
                            color = NeutralSubText,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                is DeviceDetailUiState.Success -> {
                    DeviceDetailContent(
                        data = state.data,
                        onBack = onBack
                    )
                }
            }
        }
    }
}

@Composable
private fun DeviceDetailContent(data: DeviceDetailData, onBack: () -> Unit) {
    val isOnline = data.heartbeatStatus?.uppercase() == "ONLINE"

    val components = listOf(
        ComponentInfo("카메라", Icons.Outlined.Videocam,  data.cameraStatus?.uppercase()?.let { it == "ONLINE" }),
        ComponentInfo("마이크", Icons.Outlined.Mic,       data.micStatus?.uppercase()?.let { it == "ONLINE" }),
        ComponentInfo("보드",   Icons.Outlined.Computer,  data.boardStatus?.uppercase()?.let { it == "ONLINE" })
    )

    val statusHistory = data.statusChangeLogs.map { it.toStatusHistoryEntry() }

    LazyColumn(
        contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp,
            top = 16.dp, bottom = 32.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            DeviceOverviewCard(
                deviceName     = data.deviceName,
                deviceLocation = data.locationName,
                isOnline       = isOnline,
                components     = components
            )
        }

        if (!isOnline) {
            item { TroubleshootingCard() }
        }

        item {
            InfoSection(
                title = "장치 정보",
                rows = listOf("시리얼 번호" to data.deviceSerialNumber)
            )
        }

        if (statusHistory.isNotEmpty()) {
            item { StatusHistorySection(entries = statusHistory) }
        }

        if (!isOnline) {
            item { ActionButtons() }
        }
    }
}

private fun StatusChangeLogData.toStatusHistoryEntry(): StatusHistoryEntry {
    val label       = componentType.toComponentLabel()
    val curIsOnline = currentStatus?.uppercase() == "ONLINE"
    val dotColor    = if (curIsOnline) StatusOnline else DangerContent
    val title       = "$label ${currentStatus ?: "-"}"
    val description = "${beforeStatus ?: "-"} → ${currentStatus ?: "-"}"
    val timeText    = changedAt.toTimeAgoText()
    return StatusHistoryEntry(dotColor, title, description, timeText)
}

// ─── Top Bar ─────────────────────────────────────────────────────────────────

@Composable
private fun DeviceDetailTopBar(deviceName: String, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable { onBack() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                imageVector        = Icons.AutoMirrored.Outlined.ArrowBackIos,
                contentDescription = "뒤로",
                tint               = InfoContent,
                modifier           = Modifier.size(14.dp)
            )
            Text(
                text       = "기기 목록",
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Bold,
                fontSize   = 14.sp,
                color      = InfoContent
            )
        }

        if (deviceName.isNotEmpty()) {
            Text(
                text       = deviceName,
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 17.sp,
                color      = NeutralText,
                modifier   = Modifier.align(Alignment.Center)
            )
        }
    }
}

// ─── Device Overview Card ─────────────────────────────────────────────────────

@Composable
private fun DeviceOverviewCard(
    deviceName: String,
    deviceLocation: String,
    isOnline: Boolean,
    components: List<ComponentInfo>
) {
    val cardBg      = if (isOnline) SafeCardBg     else DangerCardBg
    val iconBg      = if (isOnline) SafeChipBg     else DangerChipBg
    val accentColor = if (isOnline) SafeCardAccent else DangerCardAccent
    val statusText  = if (isOnline) "연결 중"       else "연결 끊김"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cardBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Outlined.Videocam,
                    contentDescription = null,
                    tint               = accentColor,
                    modifier           = Modifier.size(26.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text       = deviceName,
                    fontFamily = NanumSquareRound,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 17.sp,
                    color      = accentColor
                )
                Text(
                    text       = deviceLocation,
                    fontFamily = NanumSquareRound,
                    fontWeight = FontWeight.Normal,
                    fontSize   = 12.sp,
                    color      = accentColor.copy(alpha = 0.65f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(accentColor, CircleShape)
                )
                Text(
                    text       = statusText,
                    fontFamily = NanumSquareRound,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 13.sp,
                    color      = accentColor
                )
            }
        }

        ComponentTileGrid(components = components)
    }
}

// ─── 2×2 Component Grid ───────────────────────────────────────────────────────

@Composable
private fun ComponentTileGrid(components: List<ComponentInfo>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        components.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { component ->
                    ComponentTile(component = component, modifier = Modifier.weight(1f))
                }
                repeat(2 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun ComponentTile(component: ComponentInfo, modifier: Modifier = Modifier) {
    val (statusLabel, statusColor) = when (component.isOnline) {
        true  -> "ONLINE"  to SafeCardAccent
        false -> "OFFLINE" to DangerCardAccent
        null  -> "–"       to NeutralSubText
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.55f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(
            imageVector        = component.icon,
            contentDescription = component.label,
            tint               = NeutralText,
            modifier           = Modifier.size(22.dp)
        )
        Text(
            text       = component.label,
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Normal,
            fontSize   = 12.sp,
            color      = NeutralText
        )
        Text(
            text       = statusLabel,
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.ExtraBold,
            fontSize   = 13.sp,
            color      = statusColor
        )
    }
}

// ─── Info Section ─────────────────────────────────────────────────────────────

@Composable
private fun InfoSection(title: String, rows: List<Pair<String, String>>) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text       = title,
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Bold,
            fontSize   = 13.sp,
            color      = NeutralSubText,
            modifier   = Modifier.padding(horizontal = 4.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
        ) {
            rows.forEachIndexed { index, (label, value) ->
                InfoRowItem(label = label, value = value)
                if (index != rows.lastIndex) {
                    HorizontalDivider(
                        color    = Color(0xFFF0F2F5),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRowItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            text       = label,
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Normal,
            fontSize   = 14.sp,
            color      = NeutralText
        )
        Text(
            text       = value,
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.ExtraBold,
            fontSize   = 14.sp,
            color      = NeutralText
        )
    }
}

// ─── Troubleshooting Card (오프라인 전용) ─────────────────────────────────────

@Composable
private fun TroubleshootingCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFFEDED))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text       = "점검 방법",
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.ExtraBold,
            fontSize   = 15.sp,
            color      = DangerCardAccent
        )
        listOf(
            "① 전원이 연결되어 있는지 확인",
            "② 장치와 공유기가 같은 네트워크인지 확인",
            "③ 10초 후 재연결 버튼을 눌러보세요"
        ).forEach { step ->
            Text(
                text       = step,
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Normal,
                fontSize   = 13.sp,
                color      = DangerCardAccent.copy(alpha = 0.8f)
            )
        }
    }
}

// ─── Status History ───────────────────────────────────────────────────────────

@Composable
private fun StatusHistorySection(entries: List<StatusHistoryEntry>) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text       = "최근 상태 변경 이력",
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Bold,
            fontSize   = 13.sp,
            color      = NeutralSubText,
            modifier   = Modifier.padding(horizontal = 4.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
        ) {
            entries.forEachIndexed { index, entry ->
                StatusHistoryRow(entry = entry)
                if (index != entries.lastIndex) {
                    HorizontalDivider(
                        color    = Color(0xFFF0F2F5),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusHistoryRow(entry: StatusHistoryEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(8.dp)
                .background(entry.dotColor, CircleShape)
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text       = entry.title,
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Bold,
                fontSize   = 14.sp,
                color      = NeutralText
            )
            Text(
                text       = entry.description,
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Normal,
                fontSize   = 12.sp,
                color      = NeutralSubText
            )
        }
        Text(
            text       = entry.timeText,
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Normal,
            fontSize   = 12.sp,
            color      = NeutralSubText
        )
    }
}

// ─── Action Buttons (오프라인 전용) ───────────────────────────────────────────

@Composable
private fun ActionButtons() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(
            onClick  = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape  = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
        ) {
            Text(
                text       = "재연결 시도",
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp,
                color      = Color.White
            )
        }

        OutlinedButton(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape  = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Color(0xFFDDE1E7)),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
        ) {
            Text(
                text       = "지원 문의하기",
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp,
                color      = NeutralText
            )
        }
    }
}
