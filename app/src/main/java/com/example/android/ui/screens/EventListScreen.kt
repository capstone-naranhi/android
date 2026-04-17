package com.example.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.android.data.model.EventItem
import com.example.android.data.model.EventSeverity
import com.example.android.data.model.EventType
import com.example.android.ui.components.EventFilter
import com.example.android.ui.components.EventFilterRow
import com.example.android.ui.components.BottomNavigationBar
import com.example.android.ui.components.BottomNavigationItemType
import com.example.android.ui.components.EventCard
import com.example.android.ui.components.TopAppBar
import com.example.android.ui.theme.AndroidTheme
import com.example.android.ui.theme.Dimens
import com.example.android.ui.theme.StatusOnline

private fun sampleAlertEvents(): List<EventItem> = listOf(
    EventItem(
        id = "1",
        eventType = EventType.SUFFOCATION,
        timeText = "오늘 오전 10:23",
        isRead = false,
        messageOverride = "뒤집힌 자세가 감지되었습니다"
    ),
    EventItem(
        id = "2",
        eventType = EventType.CRYING,
        timeText = "오늘 오전 9:48",
        isRead = false
    ),
    EventItem(
        id = "3",
        eventType = EventType.FUSSING,
        timeText = "오늘 오전 9:15",
        isRead = true
    ),
    EventItem(
        id = "4",
        eventType = EventType.CLIMBING,
        timeText = "어제 오후 3:32",
        isRead = true,
        messageOverride = "가구 등반 의심 동작이 감지되었습니다"
    ),
    EventItem(
        id = "5",
        eventType = EventType.CRYING,
        timeText = "어제 오후 1:10",
        isRead = true
    ),
    EventItem(
        id = "6",
        eventType = EventType.FALL,
        timeText = "어제 오전 8:05",
        isRead = true
    ),
    EventItem(
        id = "7",
        eventType = EventType.SCREAM,
        timeText = "2일 전 오후 4:55",
        isRead = true
    ),
)

@Composable
fun EventListScreen() {
    var selectedFilter by remember { mutableStateOf(EventFilter.ALL) }

    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background

    val allEvents = sampleAlertEvents()
    val filteredEvents = when (selectedFilter) {
        EventFilter.ALL -> allEvents
        EventFilter.DANGER -> allEvents.filter {
            (it.severityOverride ?: it.eventType.defaultSeverity) == EventSeverity.DANGER
        }
        EventFilter.WARNING -> allEvents.filter {
            (it.severityOverride ?: it.eventType.defaultSeverity) == EventSeverity.WARNING
        }
        EventFilter.INFO -> allEvents.filter {
            (it.severityOverride ?: it.eventType.defaultSeverity) == EventSeverity.INFO
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = BottomNavigationItemType.EVENTS,
                onItemSelected = { /* TODO: Navigation */ },
                unreadEventCount = allEvents.count { !it.isRead }
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
                    title = "이벤트 모아보기",
                    subtitle = "실시간 스트리밍 중",
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
                Column(modifier = Modifier.fillMaxSize()) {
                    EventFilterRow(
                        selectedFilter = selectedFilter,
                        onFilterSelected = { selectedFilter = it }
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentPadding = PaddingValues(Dimens.spaceXl),
                        verticalArrangement = Arrangement.spacedBy(Dimens.spaceM)
                    ) {
                        items(filteredEvents, key = { it.id }) { event ->
                            EventCard(
                                event = event,
                                onClick = { /* TODO: 상세 화면 이동 */ }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EventListScreenPreview() {
    AndroidTheme {
        EventListScreen()
    }
}
