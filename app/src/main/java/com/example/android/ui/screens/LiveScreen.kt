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
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android.data.model.ActivityItem
import com.example.android.ui.components.ActivityRow
import com.example.android.ui.components.BottomNavigationBar
import com.example.android.ui.components.BottomNavigationItemType
import com.example.android.ui.components.InfoCard
import com.example.android.ui.components.VideoPlayerCard
import com.example.android.ui.theme.AndroidTheme
import com.example.android.ui.theme.AppBackground
import com.example.android.ui.theme.NanumSquareRound
import com.example.android.ui.theme.NeutralSubText
import com.example.android.ui.theme.NeutralSurface
import com.example.android.ui.theme.NeutralText
import com.example.android.ui.theme.SafeCardAccent
import com.example.android.ui.theme.StatusOnline
import kotlinx.coroutines.delay

private val sampleActivities = listOf(
    ActivityItem("14:32", "모니터링 정상"),
    ActivityItem("14:05", "칭얼거림 감지"),
    ActivityItem("13:48", "울음 감지"),
)

/** 실시간 모니터링 화면 */
@Composable
fun LiveScreen() {
    var showInfoCard by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(400)
        showInfoCard = true
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = BottomNavigationItemType.LIVE,
                onItemSelected = {}
            )
        },
        containerColor = AppBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.statusBarsPadding())
                HorizontalDivider(color = NeutralSurface, thickness = 1.dp)

                LiveTitleRow(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp))

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
                    item { VideoPlayerCard() }
                    item { CurrentStatusCard() }
                    item { RecentActivitySection(activities = sampleActivities) }
                }
            }

            // 하단 플로팅 안내 카드
            AnimatedVisibility(
                visible = showInfoCard,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
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

// ─── Title row ────────────────────────────────────────────────────────────────

@Composable
private fun LiveTitleRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "실시간 모니터링",
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = NeutralText
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(StatusOnline)
                )
                Text(
                    text = "스트리밍 중",
                    fontFamily = NanumSquareRound,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = StatusOnline
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(NeutralSurface),
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
                    .background(NeutralSurface),
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

// ─── Current status card ──────────────────────────────────────────────────────

@Composable
private fun CurrentStatusCard(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 상태 표시 원형 인디케이터
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(SafeCardAccent.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(SafeCardAccent)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = "이상 없음",
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = NeutralText
            )
            Text(
                text = "마지막 확인: 방금 전",
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = NeutralSubText
            )
        }

        // 상태 배지
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(SafeCardAccent.copy(alpha = 0.13f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "안전",
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = SafeCardAccent
            )
        }
    }
}

// ─── Recent activity section ──────────────────────────────────────────────────

@Composable
private fun RecentActivitySection(
    activities: List<ActivityItem>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "최근 활동",
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = NeutralText
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(vertical = 4.dp)
        ) {
            activities.forEachIndexed { index, activity ->
                ActivityRow(activity = activity)
                if (index != activities.lastIndex) {
                    HorizontalDivider(
                        color = NeutralSurface,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LiveScreenPreview() {
    AndroidTheme {
        LiveScreen()
    }
}
