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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.SentimentVeryDissatisfied
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android.data.model.NotificationCategory
import com.example.android.data.model.NotificationItem
import com.example.android.ui.components.BottomNavigationBar
import com.example.android.ui.components.BottomNavigationItemType
import com.example.android.ui.components.NotificationCard
import com.example.android.ui.components.NotificationFilter
import com.example.android.ui.components.NotificationFilterRow
import com.example.android.ui.theme.AndroidTheme
import com.example.android.ui.theme.AppBackground
import com.example.android.ui.theme.NanumSquareRound
import com.example.android.ui.theme.NeutralSubText
import com.example.android.ui.theme.NeutralSurface
import com.example.android.ui.theme.NeutralText

// ─── 샘플 데이터 ──────────────────────────────────────────────────────────────

private val sampleNotifications = listOf(
    NotificationItem(
        id          = "1",
        category    = NotificationCategory.SAFETY_DANGER,
        icon        = Icons.Outlined.Warning,
        badgeLabel  = "위험",
        description = "아기방 카메라 · 즉시 확인 필요",
        timeText    = "방금",
        isRead      = false
    ),
    NotificationItem(
        id          = "2",
        category    = NotificationCategory.SAFETY_CAUTION,
        icon        = Icons.Outlined.SentimentVeryDissatisfied,
        badgeLabel  = "주의",
        description = "아기방 카메라 · 3분 이상 지속",
        timeText    = "14분 전",
        isRead      = false
    ),
    NotificationItem(
        id          = "3",
        category    = NotificationCategory.DEVICE,
        icon        = Icons.Outlined.Videocam,
        badgeLabel  = "장치",
        description = "거실 카메라",
        timeText    = "1시간 전",
        isRead      = true
    ),
    NotificationItem(
        id          = "4",
        category    = NotificationCategory.REPORT,
        icon        = Icons.Outlined.Assessment,
        badgeLabel  = "리포트",
        description = "총 수면 11시간 · 수면 질 양호",
        timeText    = "오전 8시",
        isRead      = true
    ),
    NotificationItem(
        id          = "5",
        category    = NotificationCategory.AD,
        icon        = Icons.Outlined.PlayCircle,
        badgeLabel  = "일반",
        title       = "프리미엄 플랜 7일 무료 체험",
        description = "지금 시작하면 첫 달 50% 할인",
        timeText    = "어제",
        isRead      = true
    ),
)

// ─── 화면 ─────────────────────────────────────────────────────────────────────

@Composable
fun NotificationListScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToLive: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToMyPage: () -> Unit = {},
    onNotificationClick: (NotificationItem) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf(NotificationFilter.ALL) }

    val all = sampleNotifications
    val unreadCount = all.count { !it.isRead }

    val filteredItems = when (selectedFilter) {
        NotificationFilter.ALL     -> all
        NotificationFilter.SAFETY  -> all.filter {
            it.category in setOf(
                NotificationCategory.SAFETY_DANGER,
                NotificationCategory.SAFETY_CAUTION,
                NotificationCategory.SAFETY_INFO
            )
        }
        NotificationFilter.DEVICE  -> all.filter { it.category == NotificationCategory.DEVICE }
        NotificationFilter.GENERAL -> all.filter {
            it.category in setOf(NotificationCategory.REPORT, NotificationCategory.AD)
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = BottomNavigationItemType.NOTIFICATIONS,
                onItemSelected = { item ->
                    when (item) {
                        BottomNavigationItemType.HOME -> onNavigateToHome()
                        BottomNavigationItemType.LIVE -> onNavigateToLive()
                        else -> {}
                    }
                },
                unreadNotificationCount = unreadCount
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

            NotificationTitleRow(
                onSettingsClick = onNavigateToSettings,
                onProfileClick  = onNavigateToMyPage,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )

            HorizontalDivider(color = NeutralSurface, thickness = 1.dp)

            // 필터 칩 (상단 고정)
            NotificationFilterRow(
                selectedFilter   = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )

            // 알림 목록
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 20.dp, end = 20.dp, bottom = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredItems, key = { it.id }) { notification ->
                    NotificationCard(
                        notification = notification,
                        onClick = { onNotificationClick(notification) }
                    )
                }
            }
        }
    }
}

// ─── 타이틀 행 ────────────────────────────────────────────────────────────────

@Composable
private fun NotificationTitleRow(
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
            text = "알림",
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            color = NeutralText
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconCircleButton(icon = Icons.Outlined.Settings, contentDescription = "설정", onClick = onSettingsClick)
            IconCircleButton(icon = Icons.Default.Person, contentDescription = "프로필", onClick = onProfileClick)
        }
    }
}

@Composable
private fun IconCircleButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(NeutralSurface)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = NeutralSubText,
            modifier = Modifier.size(20.dp)
        )
    }
}

// ─── 프리뷰 ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NotificationListScreenPreview() {
    AndroidTheme {
        NotificationListScreen()
    }
}
