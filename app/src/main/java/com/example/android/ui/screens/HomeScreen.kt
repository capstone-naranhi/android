package com.example.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.android.data.model.EventItem
import com.example.android.data.model.EventType
import com.example.android.ui.components.BottomNavigationBar
import com.example.android.ui.components.BottomNavigationItemType
import com.example.android.ui.components.DeviceStatusCard
import com.example.android.ui.components.EventCard
import com.example.android.ui.components.SectionHeader
import com.example.android.ui.components.StatusCard
import com.example.android.ui.components.TodaySummaryCard
import com.example.android.ui.components.TodaySummaryMetric
import com.example.android.ui.components.TopAppBar
import com.example.android.ui.components.sampleDeviceStatusItems
import com.example.android.ui.theme.AndroidTheme
import com.example.android.ui.theme.DangerContent
import com.example.android.ui.theme.Dimens
import com.example.android.ui.theme.InfoContent
import com.example.android.ui.theme.StatusOnline
import com.example.android.ui.theme.StatusType
import com.example.android.ui.theme.SuccessContent
import com.example.android.ui.theme.WarningContent

/** 홈화면 */
@Composable
fun HomeScreen() {
    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = BottomNavigationItemType.HOME,
                onItemSelected = { /* TODO: Navigation Compose 연결 */ },
                unreadEventCount = 3   // 읽지 않은 모니터링 이벤트 수
            )
        },
        containerColor = backgroundColor
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            // 뒤쪽 레이어: 상단 배경
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(primaryColor)
            ) {
                Spacer(modifier = Modifier.statusBarsPadding())
                TopAppBar(
                    title = "앱 이름",
                    subtitle = "실시간 모니터링 중",
                    subtitleBadgeColor = StatusOnline,
                    showProfileButton = true,
                    showNotificationButton = true,
                    unreadNotificationCount = 2,
                    onNotificationClick = {}
                )
                Spacer(modifier = Modifier.height(40.dp))
            }

            // 앞쪽 레이어: 본문 카드
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 150.dp)
                    .padding(bottom = innerPadding.calculateBottomPadding()),
                shape = RoundedCornerShape(
                    topStart = Dimens.radiusPage,
                    topEnd = Dimens.radiusPage
                )
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = Dimens.spaceXl,
                        end = Dimens.spaceXl,
                        top = Dimens.spaceXxl,
                        bottom = Dimens.spaceXxl
                    ),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spaceL)
                ) {
                    item {
                        SectionHeader(title = "우리 아이 상태")
                        StatusCard(
                            title = "안전",
                            subtitle = "마지막 확인: 방금 전",
                            statusType = StatusType.SUCCESS
                        )
                    }

                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceM)) {
                            SectionHeader(
                                title = "최근 이벤트",
                                actionText = "전체보기",
                                onActionClick = { /* TODO: 이벤트 목록 이동 */ }
                            )
                            EventCard(
                                event = EventItem(
                                    id = "1",
                                    eventType = EventType.FUSSING,
                                    timeText = "5분 전",
                                    isRead = false
                                )
                            )
                            EventCard(
                                event = EventItem(
                                    id = "2",
                                    eventType = EventType.CRYING,
                                    timeText = "23분 전",
                                    isRead = true
                                )
                            )
                            EventCard(
                                event = EventItem(
                                    id = "3",
                                    eventType = EventType.SUFFOCATION,
                                    timeText = "오늘 오전 10:23",
                                    isRead = false
                                )
                            )
                        }
                    }

                    item {
                        SectionHeader(
                            title = "오늘의 요약",
                            statusText = "분석중",
                            statusColor = SuccessContent
                        )
                        Spacer(modifier = Modifier.height(Dimens.spaceM))
                        TodaySummaryCard(
                            metrics = listOf(
                                TodaySummaryMetric(
                                    label = "위험 감지",
                                    valueText = "2회",
                                    valueColor = DangerContent
                                ),
                                TodaySummaryMetric(
                                    label = "울음",
                                    valueText = "3회",
                                    valueColor = WarningContent
                                ),
                                TodaySummaryMetric(
                                    label = "칭얼거림",
                                    valueText = "7회",
                                    valueColor = InfoContent
                                ),
                                TodaySummaryMetric(
                                    label = "비명",
                                    valueText = "0회",
                                    valueColor = SuccessContent
                                ),
                            )
                        )
                    }

                    item {
                        SectionHeader(
                            title = "장치 상태",
                            actionText = "상세보기",
                            onActionClick = { /* TODO: 장치 상태 상세 화면 이동 */ }
                        )
                        Spacer(modifier = Modifier.height(Dimens.spaceM))
                        DeviceStatusCard(devices = sampleDeviceStatusItems())
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    AndroidTheme {
        HomeScreen()
    }
}