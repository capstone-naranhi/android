package com.example.android.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.android.data.model.ActivityItem
import com.example.android.ui.components.ActivityRow
import com.example.android.ui.components.BottomNavigationBar
import com.example.android.ui.components.BottomNavigationItemType
import com.example.android.ui.components.InfoCard
import com.example.android.ui.components.SectionHeader
import com.example.android.ui.components.StatusCard
import com.example.android.ui.components.TopAppBar
import com.example.android.ui.components.VideoPlayerCard
import com.example.android.ui.theme.AndroidTheme
import com.example.android.ui.theme.Dimens
import com.example.android.ui.theme.StatusOnline
import com.example.android.ui.theme.StatusType
import kotlinx.coroutines.delay

private val sampleActivities = listOf(
    ActivityItem("14:32", "모니터링 정상"),
    ActivityItem("14:05", "칭얼거림 감지"),
    ActivityItem("13:48", "울음 감지"),
)

/** 실시간 모니터링 화면 */
@Composable
fun LiveScreen() {
    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background

    var showInfoCard by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(400)
        showInfoCard = true
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = BottomNavigationItemType.LIVE,
                onItemSelected = { /* TODO: Navigation Compose 연결 */ }
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
                    title = "실시간 모니터링",
                    subtitle = "실시간 스트리밍 중",
                    subtitleBadgeColor = StatusOnline
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
                    item { VideoPlayerCard() }
                    item {
                        StatusCard(
                            title = "이상 없음",
                            subtitle = "마지막 확인: 방금 전",
                            statusType = StatusType.SUCCESS
                        )
                    }
                    item { RecentActivitySection(activities = sampleActivities) }
                }
            }

            // 인포메이션 카드: 하단 탭 바 바로 위 고정
            AnimatedVisibility(
                visible = showInfoCard,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(
                        start = Dimens.spaceXl,
                        end = Dimens.spaceXl,
                        bottom = innerPadding.calculateBottomPadding() + Dimens.spaceL
                    ),
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = 400)
                ) + fadeIn(animationSpec = tween(durationMillis = 300)),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeOut(animationSpec = tween(durationMillis = 200))
            ) {
                InfoCard(onDismiss = { showInfoCard = false })
            }
        }
    }
}

/** 최근 활동 섹션 */
@Composable
private fun RecentActivitySection(
    activities: List<ActivityItem>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionHeader(title = "최근 활동")
        Spacer(modifier = Modifier.height(Dimens.spaceM))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimens.radiusCard),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = Dimens.spaceXs)) {
                activities.forEachIndexed { index, activity ->
                    ActivityRow(activity = activity)
                    if (index != activities.lastIndex) {
                        HorizontalDivider(
                            color = Color(0xFFF0F2F5),
                            modifier = Modifier.padding(horizontal = Dimens.spaceL)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LiveScreenPreview() {
    AndroidTheme {
        LiveScreen()
    }
}
