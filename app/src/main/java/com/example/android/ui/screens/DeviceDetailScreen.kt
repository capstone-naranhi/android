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
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.android.ui.theme.WarningContent

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

// ─── Sample Data ──────────────────────────────────────────────────────────────

private fun onlineComponents() = listOf(
    ComponentInfo("카메라",   Icons.Outlined.Videocam,   true),
    ComponentInfo("마이크",   Icons.Outlined.Mic,        true),
    ComponentInfo("보드",     Icons.Outlined.Computer,   true),
    ComponentInfo("추론 모듈", Icons.Outlined.Psychology, true)
)

private fun offlineComponents() = listOf(
    ComponentInfo("카메라",   Icons.Outlined.Videocam,   false),
    ComponentInfo("마이크",   Icons.Outlined.Mic,        false),
    ComponentInfo("보드",     Icons.Outlined.Computer,   false),
    ComponentInfo("추론 모듈", Icons.Outlined.Psychology, null)
)

private fun onlineHistory() = listOf(
    StatusHistoryEntry(StatusOnline,    "카메라 ONLINE",  "OFFLINE → ONLINE",              "10:30"),
    StatusHistoryEntry(DangerContent,   "카메라 OFFLINE", "네트워크 순단",                   "10:28"),
    StatusHistoryEntry(StatusOnline,    "보드 ONLINE",    "재부팅 완료",                     "어제")
)

private fun offlineHistory() = listOf(
    StatusHistoryEntry(DangerContent,   "전체 OFFLINE",  "네트워크 연결 끊김",                "09:14"),
    StatusHistoryEntry(StatusOnline,    "보드 ONLINE",   "정상 가동 중",                     "어제"),
    StatusHistoryEntry(WarningContent,  "마이크 순단",    "ONLINE → OFFLINE → ONLINE",       "3일 전")
)

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun DeviceDetailScreen(
    deviceName: String = "아기방",
    deviceLocation: String = "아기 침실 천장",
    isOnline: Boolean = true,
    cpuUsage: String = "34%",
    memoryUsage: String = "51%",
    lastSignalTime: String = "오전 9:14 (1시간 전)",
    lastEventTime: String = "10분 전",
    serialNumber: String = "JTN-20240812-001",
    mqttClientId: String = "baby-jetson-001",
    registeredDate: String = "2024. 08. 20",
    onBack: () -> Unit = {}
) {
    val components    = if (isOnline) onlineComponents()  else offlineComponents()
    val statusHistory = if (isOnline) onlineHistory()     else offlineHistory()

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

            // 상단 네비게이션 바
            DeviceDetailTopBar(deviceName = deviceName, onBack = onBack)
            HorizontalDivider(color = Color(0xFFF0F2F5))

            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp,
                    top = 16.dp, bottom = 32.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 장치 개요 카드
                item {
                    DeviceOverviewCard(
                        deviceName     = deviceName,
                        deviceLocation = deviceLocation,
                        isOnline       = isOnline,
                        components     = components,
                        lastSignalText = if (!isOnline) lastSignalTime else null
                    )
                }

                // 시스템 현황 (온라인 전용)
                if (isOnline) {
                    item {
                        InfoSection(
                            title = "시스템 현황",
                            rows = listOf(
                                "CPU 사용률"    to cpuUsage,
                                "메모리 사용률" to memoryUsage,
                                "마지막 신호 확인" to "방금 전",   // 하트비트 → 마지막 신호 확인
                                "마지막 이벤트" to lastEventTime
                            )
                        )
                    }
                }

                // 점검 방법 카드 (오프라인 전용)
                if (!isOnline) {
                    item { TroubleshootingCard() }
                }

                // 장치 정보
                item {
                    InfoSection(
                        title = "장치 정보",
                        rows = listOf(
                            "시리얼 번호"     to serialNumber,
                            "MQTT Client ID" to mqttClientId,
                            "등록일"          to registeredDate
                        )
                    )
                }

                // 최근 상태 변경 이력
                item {
                    StatusHistorySection(entries = statusHistory)
                }

                // 재연결 / 지원 문의 버튼 (오프라인 전용)
                if (!isOnline) {
                    item {
                        ActionButtons()
                    }
                }
            }
        }
    }
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
                imageVector     = Icons.AutoMirrored.Outlined.ArrowBackIos,
                contentDescription = "뒤로",
                tint            = InfoContent,
                modifier        = Modifier.size(14.dp)
            )
            Text(
                text       = "기기 목록",
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Bold,
                fontSize   = 14.sp,
                color      = InfoContent
            )
        }

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

// ─── Device Overview Card ─────────────────────────────────────────────────────

@Composable
private fun DeviceOverviewCard(
    deviceName: String,
    deviceLocation: String,
    isOnline: Boolean,
    components: List<ComponentInfo>,
    lastSignalText: String?
) {
    val cardBg       = if (isOnline) SafeCardBg     else DangerCardBg
    val iconBg       = if (isOnline) SafeChipBg     else DangerChipBg
    val accentColor  = if (isOnline) SafeCardAccent else DangerCardAccent
    val statusText   = if (isOnline) "연결 중"       else "연결 끊김"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cardBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 헤더: 아이콘 + 이름/위치 + 상태 배지
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

            // 연결 상태 배지
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

        // 2×2 컴포넌트 그리드
        ComponentTileGrid(components = components)

        // 마지막 신호 확인 (오프라인 전용, 기존 '마지막 하트비트' 대체)
        if (lastSignalText != null) {
            Text(
                text       = "마지막 신호 확인: $lastSignalText",
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Normal,
                fontSize   = 12.sp,
                color      = accentColor.copy(alpha = 0.72f)
            )
        }
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

// ─── Info Section (시스템 현황 / 장치 정보 공용) ───────────────────────────────

@Composable
private fun InfoSection(
    title: String,
    rows: List<Pair<String, String>>
) {
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

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true, name = "정상 상태")
@Composable
fun DeviceDetailOnlinePreview() {
    AndroidTheme {
        DeviceDetailScreen(
            deviceName     = "아기방",
            deviceLocation = "아기 침실 천장",
            isOnline       = true
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "오프라인 상태")
@Composable
fun DeviceDetailOfflinePreview() {
    AndroidTheme {
        DeviceDetailScreen(
            deviceName     = "거실",
            deviceLocation = "거실 선반 위",
            isOnline       = false
        )
    }
}
