package com.example.android.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.android.data.model.ActivityItem
import com.example.android.data.model.LiveStreamStatusData
import com.example.android.ui.components.ActivityRow
import com.example.android.ui.components.BottomNavigationBar
import com.example.android.ui.components.BottomNavigationItemType
import com.example.android.ui.components.InfoCard
import com.example.android.ui.components.VideoPlayerCard
import com.example.android.ui.theme.AndroidTheme
import com.example.android.ui.theme.AppBackground
import com.example.android.ui.theme.DangerContent
import com.example.android.ui.theme.NanumSquareRound
import com.example.android.ui.theme.NeutralSubText
import com.example.android.ui.theme.NeutralSurface
import com.example.android.ui.theme.NeutralText
import com.example.android.ui.theme.SafeCardAccent
import com.example.android.ui.theme.StatusOnline
import kotlinx.coroutines.delay

/** 실시간 모니터링 화면 */
@Composable
fun LiveScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToMyPage: () -> Unit = {},
    viewModel: LiveViewModel = viewModel()
) {
    val uiState          by viewModel.uiState.collectAsState()
    val videoTrack       by viewModel.videoTrack.collectAsState()
    val liveStreamStatus by viewModel.liveStreamStatus.collectAsState()
    val activities       by viewModel.activities.collectAsState()

    // 화면 진입 시 스트리밍 시작, 이탈 시 중지
    LaunchedEffect(Unit) { viewModel.startStream() }
    DisposableEffect(Unit) { onDispose { viewModel.stopStream() } }

    var showInfoCard by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(400)
        showInfoCard = true
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = BottomNavigationItemType.LIVE,
                onItemSelected = { item ->
                    when (item) {
                        BottomNavigationItemType.HOME          -> onNavigateToHome()
                        BottomNavigationItemType.NOTIFICATIONS -> onNavigateToNotifications()
                        else -> {}
                    }
                }
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

                LiveTitleRow(
                    connectionState = uiState.connectionState,
                    onSettingsClick = onNavigateToSettings,
                    onProfileClick  = onNavigateToMyPage,
                    modifier        = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )

                HorizontalDivider(color = NeutralSurface, thickness = 1.dp)

                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 20.dp, end = 20.dp,
                        top = 20.dp, bottom = 32.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item {
                        VideoPlayerCard(
                            videoTrack     = videoTrack,
                            eglBaseContext = viewModel.getEglBaseContext()
                        )
                    }

                    // 에러 메시지
                    if (uiState.connectionState == LiveConnectionState.ERROR) {
                        item {
                            ErrorBanner(
                                message = uiState.errorMessage ?: "연결 오류",
                                onRetry = { viewModel.startStream() }
                            )
                        }
                    }

                    item { CurrentStatusCard(liveStreamStatus = liveStreamStatus) }
                    if (activities.isNotEmpty()) {
                        item { RecentActivitySection(activities = activities) }
                    }
                }
            }

            AnimatedVisibility(
                visible  = showInfoCard,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec  = tween(durationMillis = 400)
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
private fun LiveTitleRow(
    connectionState: LiveConnectionState,
    onSettingsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val (dotColor, statusText) = when (connectionState) {
        LiveConnectionState.STREAMING   -> StatusOnline to "스트리밍 중"
        LiveConnectionState.CONNECTING  -> NeutralSubText to "연결 중..."
        LiveConnectionState.LOADING     -> NeutralSubText to "준비 중..."
        LiveConnectionState.ERROR       -> DangerContent to "연결 오류"
        LiveConnectionState.DISCONNECTED -> DangerContent to "연결 끊김"
        LiveConnectionState.IDLE        -> NeutralSubText to "대기 중"
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text       = "실시간 모니터링",
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 20.sp,
                color      = NeutralText
            )
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
                Text(
                    text       = statusText,
                    fontFamily = NanumSquareRound,
                    fontWeight = FontWeight.Normal,
                    fontSize   = 13.sp,
                    color      = dotColor
                )
            }
        }

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
                    imageVector        = Icons.Outlined.Settings,
                    contentDescription = "설정",
                    tint               = NeutralSubText,
                    modifier           = Modifier.size(20.dp)
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
                    imageVector        = Icons.Default.Person,
                    contentDescription = "프로필",
                    tint               = NeutralSubText,
                    modifier           = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ─── Error banner ─────────────────────────────────────────────────────────────

@Composable
private fun ErrorBanner(message: String, onRetry: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DangerContent.copy(alpha = 0.1f))
            .clickable { onRetry() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            text       = message,
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Normal,
            fontSize   = 13.sp,
            color      = DangerContent,
            modifier   = Modifier.weight(1f)
        )
        Text(
            text       = "재시도",
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Bold,
            fontSize   = 13.sp,
            color      = DangerContent
        )
    }
}

// ─── Current status card ──────────────────────────────────────────────────────

private enum class StatusLevel { SAFE, CAUTION, DANGER, OFFLINE }

private fun resolveStatusLevel(status: LiveStreamStatusData?): StatusLevel {
    if (status == null) return StatusLevel.SAFE
    if (status.heartbeatStatus?.uppercase() != "ONLINE") return StatusLevel.OFFLINE
    val event = status.ongoingSafetyEvent ?: return StatusLevel.SAFE
    return when (event.severity?.uppercase()) {
        "DANGER"  -> StatusLevel.DANGER
        "CAUTION" -> StatusLevel.CAUTION
        else      -> StatusLevel.SAFE
    }
}

private data class StatusStyle(
    val dotColor: Color,
    val badgeBg: Color,
    val badgeColor: Color,
    val title: String,
    val badge: String
)

@Composable
private fun statusStyleFor(level: StatusLevel): StatusStyle = when (level) {
    StatusLevel.SAFE    -> StatusStyle(SafeCardAccent,  SafeCardAccent.copy(alpha = 0.13f),  SafeCardAccent,     "이상 없음",   "안전")
    StatusLevel.CAUTION -> StatusStyle(Color(0xFFFFA726), Color(0xFFFFA726).copy(alpha = 0.13f), Color(0xFFE65100), "주의 감지됨", "주의")
    StatusLevel.DANGER  -> StatusStyle(DangerContent,   DangerContent.copy(alpha = 0.13f),   DangerContent,      "위험 감지됨", "위험")
    StatusLevel.OFFLINE -> StatusStyle(NeutralSubText,  NeutralSubText.copy(alpha = 0.13f),  NeutralSubText,     "장치 오프라인", "오프라인")
}

@Composable
private fun CurrentStatusCard(
    liveStreamStatus: LiveStreamStatusData?,
    modifier: Modifier = Modifier
) {
    val level = resolveStatusLevel(liveStreamStatus)
    val style = statusStyleFor(level)

    val subText = liveStreamStatus?.ongoingSafetyEvent?.let { event ->
        event.eventType?.let { "이벤트: $it" }
    } ?: liveStreamStatus?.deviceName?.let { "$it 모니터링 중" } ?: "장치 연결 확인 중"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(style.dotColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(style.dotColor)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text       = style.title,
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 16.sp,
                color      = NeutralText
            )
            Text(
                text       = subText,
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Normal,
                fontSize   = 12.sp,
                color      = NeutralSubText
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(style.badgeBg)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text       = style.badge,
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Bold,
                fontSize   = 13.sp,
                color      = style.badgeColor
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
            text       = "최근 활동",
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Bold,
            fontSize   = 16.sp,
            color      = NeutralText
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
                        color    = NeutralSurface,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}
