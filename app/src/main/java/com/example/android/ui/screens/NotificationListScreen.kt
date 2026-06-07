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
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.android.data.model.NotificationItem
import com.example.android.ui.components.BottomNavigationBar
import com.example.android.ui.components.BottomNavigationItemType
import com.example.android.ui.components.NotificationCard
import com.example.android.ui.components.NotificationFilterRow
import com.example.android.ui.theme.AppBackground
import com.example.android.ui.theme.BrandPrimary
import com.example.android.ui.theme.NanumSquareRound
import com.example.android.ui.theme.NeutralSubText
import com.example.android.ui.theme.NeutralSurface
import com.example.android.ui.theme.NeutralText

// ─── 화면 ─────────────────────────────────────────────────────────────────────

@Composable
fun NotificationListScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToLive: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToMyPage: () -> Unit = {},
    onNotificationClick: (NotificationItem) -> Unit = {},
    viewModel: NotificationListViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

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
                unreadNotificationCount = state.unreadCount
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
                selectedFilter   = state.selectedFilter,
                onFilterSelected = { viewModel.setFilter(it) }
            )

            // 본문
            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BrandPrimary)
                    }
                }

                state.error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.error!!,
                            fontFamily = NanumSquareRound,
                            fontSize = 14.sp,
                            color = NeutralSubText,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                state.items.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "알림이 없습니다.",
                            fontFamily = NanumSquareRound,
                            fontSize = 14.sp,
                            color = NeutralSubText
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = 20.dp, end = 20.dp, bottom = 24.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.items, key = { it.id }) { notification ->
                            NotificationCard(
                                notification = notification,
                                onClick = { onNotificationClick(notification) }
                            )
                            // 마지막 아이템 도달 시 다음 페이지 로드
                            if (notification == state.items.lastOrNull() && state.hasNext) {
                                LaunchedEffect(notification.id) { viewModel.loadMore() }
                            }
                        }

                        if (state.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = BrandPrimary,
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }
                    }
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
