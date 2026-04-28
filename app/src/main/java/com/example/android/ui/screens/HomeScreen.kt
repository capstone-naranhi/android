package com.example.android.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.SentimentVeryDissatisfied
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material.icons.outlined.Warning
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android.ui.components.BottomNavigationBar
import com.example.android.ui.components.BottomNavigationItemType
import com.example.android.ui.components.DeviceStatusCard
import com.example.android.ui.components.DeviceStatusItem
import com.example.android.ui.theme.AndroidTheme
import com.example.android.ui.theme.AppBackground
import com.example.android.ui.theme.CautionCardAccent
import com.example.android.ui.theme.CautionCardBg
import com.example.android.ui.theme.CautionChipBg
import com.example.android.ui.theme.DangerCardAccent
import com.example.android.ui.theme.DangerCardBg
import com.example.android.ui.theme.DangerChipBg
import com.example.android.ui.theme.DangerContainer
import com.example.android.ui.theme.DangerContent
import com.example.android.ui.theme.DangerIconBg
import com.example.android.ui.theme.NanumSquareRound
import com.example.android.ui.theme.NeutralSubText
import com.example.android.ui.theme.NeutralSurface
import com.example.android.ui.theme.NeutralText
import com.example.android.ui.theme.SafeCardAccent
import com.example.android.ui.theme.SafeCardBg
import com.example.android.ui.theme.SafeChipBg
import com.example.android.ui.theme.StatusOnline
import com.example.android.ui.theme.StatusType
import com.example.android.ui.theme.WarningContainer
import com.example.android.ui.theme.WarningContent
import com.example.android.ui.theme.WarningIconBg

// ─── Local data models ────────────────────────────────────────────────────────

private enum class SafetyLevel { SAFE, CAUTION, DANGER }

private data class MetricData(val label: String, val value: String)

private data class CameraRoom(val name: String, val isConnected: Boolean)

private data class HomeAlert(
    val id: String,
    val level: SafetyLevel,
    val title: String,
    val room: String,
    val timeText: String
)

// ─── Sample data ──────────────────────────────────────────────────────────────

private fun safeMetrics() = listOf(
    MetricData("호흡", "정상"),
    MetricData("움직임", "감지됨"),
    MetricData("울음", "없음")
)

private fun cautionMetrics() = listOf(
    MetricData("호흡", "정상"),
    MetricData("움직임", "활발"),
    MetricData("울음", "3분 지속")
)

private fun dangerMetrics() = listOf(
    MetricData("호흡", "불규칙"),
    MetricData("움직임", "감지됨"),
    MetricData("위험", "질식")
)

private val sampleRooms = listOf(
    CameraRoom("아기방", true)
)

private val sampleAlerts = listOf(
    HomeAlert("1", SafetyLevel.DANGER, "질식 위험 감지됨", "아기방 카메라", "방금"),
    HomeAlert("2", SafetyLevel.CAUTION, "울음 감지됨", "아기방 카메라", "14분 전")
)

private val sampleDevices = listOf(
    DeviceStatusItem("보드", "연결됨", StatusType.SUCCESS, Icons.Outlined.Memory),
    DeviceStatusItem("카메라", "연결됨", StatusType.SUCCESS, Icons.Outlined.Videocam),
    DeviceStatusItem("마이크", "연결됨", StatusType.SUCCESS, Icons.Outlined.Mic)
)

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun HomeScreen(
    onNavigateToSettings: () -> Unit = {},
    onNavigateToMyPage: () -> Unit = {},
    onNavigateToLive: () -> Unit = {},
    onNavigateToNotificationList: () -> Unit = {},
    onNavigateToSafetyDetail: (String) -> Unit = {},
    onNavigateToDeviceDetail: () -> Unit = {}
) {
    // 현재 아이의 실제 상태에 따라 결정되는 값 (탭으로 전환하는 UI 없음)
    val currentLevel = SafetyLevel.SAFE

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = BottomNavigationItemType.HOME,
                onItemSelected = { item ->
                    when (item) {
                        BottomNavigationItemType.LIVE          -> onNavigateToLive()
                        BottomNavigationItemType.NOTIFICATIONS -> onNavigateToNotificationList()
                        else -> {}
                    }
                },
                unreadNotificationCount = 1
            )
        },
        containerColor = AppBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            Spacer(modifier = Modifier.statusBarsPadding())
            HorizontalDivider(color = NeutralSurface, thickness = 1.dp)

            GreetingRow(
                name = "지민맘",
                onSettingsClick = onNavigateToSettings,
                onProfileClick = onNavigateToMyPage,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = 16.dp)
            )

            HorizontalDivider(
                color = NeutralSurface,
                thickness = 1.dp
            )

            LazyColumn(
                contentPadding = PaddingValues(
                    start = 20.dp, end = 20.dp,
                    top = 20.dp, bottom = 32.dp
                ),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item { SafetyStatusCard(level = currentLevel) }
                item { StatsRow(todayAlerts = 2, todayCrying = 5) }
                item {
                    LivePreviewCard(
                        mainRoom = "아기방",
                        rooms = sampleRooms,
                        onClick = onNavigateToLive
                    )
                }
                item {
                    RecentAlertsSection(
                        alerts = sampleAlerts,
                        onViewAll = onNavigateToNotificationList,
                        onAlertClick = { alert -> onNavigateToSafetyDetail(alert.id) }
                    )
                }
                item { DeviceStatusSection(devices = sampleDevices, onViewAll = onNavigateToDeviceDetail) }
            }
        }
    }
}

// ─── Greeting row ─────────────────────────────────────────────────────────────

@Composable
private fun GreetingRow(
    name: String,
    onSettingsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "안녕하세요, $name 👋",
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            color = NeutralText
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(NeutralSurface)
                    .clickable { onSettingsClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "설정",
                    tint = NeutralSubText,
                    modifier = Modifier.size(20.dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(NeutralSurface)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "프로필",
                    tint = NeutralSubText,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ─── Safety status card ───────────────────────────────────────────────────────

private data class StatusCardStyle(
    val cardBg: Color,
    val accent: Color,
    val chipBg: Color,
    val badgeLabel: String,
    val timeLabel: String,
    val mainText: String,
    val subText: String,
    val metrics: List<MetricData>
)

private fun styleForLevel(level: SafetyLevel) = when (level) {
    SafetyLevel.SAFE -> StatusCardStyle(
        cardBg = SafeCardBg,
        accent = SafeCardAccent,
        chipBg = SafeChipBg,
        badgeLabel = "안전",
        timeLabel = "방금 확인됨",
        mainText = "아이가 안전해요",
        subText = "정상적인 호흡과 움직임이 감지되고 있어요",
        metrics = safeMetrics()
    )

    SafetyLevel.CAUTION -> StatusCardStyle(
        cardBg = CautionCardBg,
        accent = CautionCardAccent,
        chipBg = CautionChipBg,
        badgeLabel = "주의",
        timeLabel = "3분 전 감지됨",
        mainText = "울음이 감지됐어요",
        subText = "3분 이상 울음이 지속되고 있어요",
        metrics = cautionMetrics()
    )

    SafetyLevel.DANGER -> StatusCardStyle(
        cardBg = DangerCardBg,
        accent = DangerCardAccent,
        chipBg = DangerChipBg,
        badgeLabel = "위험",
        timeLabel = "방금 감지됨",
        mainText = "즉시 확인이 필요해요",
        subText = "질식 위험이 감지됐어요. 지금 바로 확인해주세요",
        metrics = dangerMetrics()
    )
}

@Composable
private fun SafetyStatusCard(level: SafetyLevel, modifier: Modifier = Modifier) {
    val s = styleForLevel(level)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(s.cardBg)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Badge + time
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusBadge(label = s.badgeLabel, accent = s.accent)
            Text(
                text = s.timeLabel,
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = s.accent.copy(alpha = 0.65f)
            )
        }

        // Circle indicator + text
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(s.accent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(s.accent)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = s.mainText,
                    fontFamily = NanumSquareRound,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = s.accent
                )
                Text(
                    text = s.subText,
                    fontFamily = NanumSquareRound,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = s.accent.copy(alpha = 0.7f)
                )
            }
        }

        // Metric chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            s.metrics.forEach { metric ->
                MetricChipItem(
                    label = metric.label,
                    value = metric.value,
                    bg = s.chipBg,
                    textColor = s.accent,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Danger CTA
        if (level == SafetyLevel.DANGER) {
            OutlinedButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = s.accent),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, s.accent)
            ) {
                Text(
                    text = "지금 바로 확인하기",
                    fontFamily = NanumSquareRound,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(label: String, accent: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(accent.copy(alpha = 0.13f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(accent)
        )
        Text(
            text = label,
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = accent
        )
    }
}

@Composable
private fun MetricChipItem(
    label: String,
    value: String,
    bg: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = label,
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            color = textColor.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = textColor
        )
    }
}

// ─── Stats row ────────────────────────────────────────────────────────────────

@Composable
private fun StatsRow(todayAlerts: Int, todayCrying: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(number = "$todayAlerts", label = "오늘 알림", modifier = Modifier.weight(1f))
        StatCard(number = "${todayCrying}회", label = "오늘 울음 감지", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(number: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = number,
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 28.sp,
            color = NeutralText
        )
        Text(
            text = label,
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            color = NeutralSubText
        )
    }
}

// ─── LIVE card ────────────────────────────────────────────────────────────────

@Composable
private fun LivePreviewCard(
    mainRoom: String,
    rooms: List<CameraRoom>,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE53935))
                )
                Text(
                    text = "LIVE",
                    fontFamily = NanumSquareRound,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    color = Color(0xFFE53935)
                )
            }
            Text(
                text = mainRoom,
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = NeutralText
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            rooms.forEach { room -> RoomStatusDot(room) }
        }
    }
}

@Composable
private fun RoomStatusDot(room: CameraRoom) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(if (room.isConnected) StatusOnline else Color(0xFFE53935))
        )
        Text(
            text = "${room.name}·${if (room.isConnected) "연결 중" else "연결 끊김"}",
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = NeutralSubText
        )
    }
}

// ─── Device status section ────────────────────────────────────────────────────

@Composable
private fun DeviceStatusSection(
    devices: List<DeviceStatusItem>,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "장치 상태",
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = NeutralText
            )
            Row(
                modifier = Modifier.clickable { onViewAll() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "상세보기",
                    fontFamily = NanumSquareRound,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = NeutralSubText
                )
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = NeutralSubText,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        DeviceStatusCard(devices = devices, isDarkTheme = false)
    }
}

// ─── Recent alerts ────────────────────────────────────────────────────────────

@Composable
private fun RecentAlertsSection(
    alerts: List<HomeAlert>,
    onViewAll: () -> Unit,
    onAlertClick: (HomeAlert) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "최근 알림",
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = NeutralText
            )
            Row(
                modifier = Modifier.clickable { onViewAll() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "상세보기",
                    fontFamily = NanumSquareRound,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = NeutralSubText
                )
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = NeutralSubText,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        alerts.forEach { alert ->
            HomeAlertCard(alert = alert, onClick = { onAlertClick(alert) })
        }
    }
}

@Composable
private fun HomeAlertCard(alert: HomeAlert, onClick: () -> Unit = {}) {
    val iconBg: Color
    val iconTint: Color
    val badgeBg: Color
    val badgeText: String

    when (alert.level) {
        SafetyLevel.DANGER -> {
            iconBg = DangerIconBg; iconTint = DangerContent
            badgeBg = DangerContainer; badgeText = "위험"
        }

        SafetyLevel.CAUTION -> {
            iconBg = WarningIconBg; iconTint = WarningContent
            badgeBg = WarningContainer; badgeText = "주의"
        }

        SafetyLevel.SAFE -> {
            iconBg = Color(0xFFF1FBF5); iconTint = Color(0xFF0F9D58)
            badgeBg = Color(0xFFDFF5E8); badgeText = "안전"
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (alert.level == SafetyLevel.DANGER)
                    Icons.Outlined.Warning
                else
                    Icons.Outlined.SentimentVeryDissatisfied,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(badgeBg)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badgeText,
                    fontFamily = NanumSquareRound,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = iconTint
                )
            }
            Text(
                text = alert.title,
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = NeutralText
            )
            Text(
                text = alert.room,
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = NeutralSubText
            )
        }

        Text(
            text = alert.timeText,
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = NeutralSubText
        )
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    AndroidTheme {
        HomeScreen()
    }
}
