package com.example.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.android.data.model.NotificationDetailData
import com.example.android.data.model.toKoreanDateTimeString
import com.example.android.data.model.toDurationText
import com.example.android.data.model.toSeverityLabel
import com.example.android.ui.theme.AppBackground
import com.example.android.ui.theme.BrandPrimary
import com.example.android.ui.theme.DangerContent
import com.example.android.ui.theme.InfoContent
import com.example.android.ui.theme.NanumSquareRound
import com.example.android.ui.theme.NeutralSubText
import com.example.android.ui.theme.NeutralText
import com.example.android.ui.theme.VideoBackground
import com.example.android.ui.theme.WarningContent

// ─── 화면 ─────────────────────────────────────────────────────────────────────

@Composable
fun SafetyNotificationDetailScreen(
    notificationId: String,
    onBack: () -> Unit = {},
    onConfirmNow: () -> Unit = {},
    viewModel: NotificationDetailViewModel = viewModel(
        key = notificationId,
        factory = NotificationDetailViewModel.factory(notificationId)
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .statusBarsPadding()
    ) {
        HorizontalDivider(color = Color(0xFFE5E9F0), thickness = 1.dp)
        DetailTopBar(title = "알림 상세", onBack = onBack)
        HorizontalDivider(color = Color(0xFFE5E9F0), thickness = 1.dp)

        when (val state = uiState) {
            is NotificationDetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandPrimary)
                }
            }

            is NotificationDetailUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
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

            is NotificationDetailUiState.Success -> {
                SafetyDetailContent(
                    modifier = Modifier.weight(1f),
                    data = state.data,
                    onConfirmNow = onConfirmNow
                )
            }
        }
    }
}

// ─── 콘텐츠 ───────────────────────────────────────────────────────────────────

@Composable
private fun SafetyDetailContent(
    data: NotificationDetailData,
    onConfirmNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    val safety = data.safetyDetail

    val severityStr = safety?.severity?.uppercase()
    val accentColor = when (severityStr) {
        "DANGER"  -> DangerContent
        "CAUTION" -> WarningContent
        else      -> InfoContent
    }

    val badgeLabel = safety?.severity.toSeverityLabel()
    val eventLabel = safety?.eventType?.label ?: "안전 이벤트"
    val deviceName = safety?.deviceName ?: "-"
    val dateTimeText = data.sentAt.toKoreanDateTimeString()
    val hasVideo = !safety?.videoUrl.isNullOrBlank()

    Column(modifier = modifier) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // 알림 요약 카드
            NotificationSummaryCard(
                accentColor = accentColor,
                badgeLabel = badgeLabel,
                title = eventLabel,
                subtitle = deviceName,
                dateTimeText = dateTimeText
            )

            // 녹화 영상 섹션
            if (hasVideo) {
                RecordedVideoSection(
                    accentColor = accentColor,
                    videoUrl = safety?.videoUrl ?: ""
                )
            }

            // 감지 정보
            DetailSection(title = "감지 정보") {
                InfoRow(label = "이벤트 유형", value = eventLabel)
                InfoDivider()
                InfoRow(
                    label = "심각도",
                    value = badgeLabel,
                    valueColor = accentColor
                )
                InfoDivider()
                InfoRow(
                    label = "지속 시간",
                    value = safety?.durationSecond.toDurationText()
                )
                InfoDivider()
                InfoRow(label = "감지 기기", value = deviceName)
                if (safety?.detectedAt != null) {
                    InfoDivider()
                    InfoRow(
                        label = "감지 시각",
                        value = safety.detectedAt.toKoreanDateTimeString()
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }

        // 하단 버튼
        Box(modifier = Modifier.navigationBarsPadding().padding(horizontal = 20.dp, vertical = 16.dp)) {
            PrimaryActionButton(
                text = "지금 바로 확인하기",
                onClick = onConfirmNow
            )
        }
    }
}

// ─── 서브 컴포넌트 ─────────────────────────────────────────────────────────────

@Composable
internal fun DetailTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "뒤로가기",
                tint = NeutralText
            )
        }
        Text(
            text = title,
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            color = NeutralText
        )
    }
}

@Composable
internal fun NotificationSummaryCard(
    accentColor: Color,
    badgeLabel: String,
    title: String,
    subtitle: String,
    dateTimeText: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(100.dp)
                .background(accentColor)
        )
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(accentColor.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = badgeLabel,
                    fontFamily = NanumSquareRound,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = accentColor
                )
            }
            Text(
                text = title,
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = NeutralText
            )
            Text(
                text = subtitle,
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = NeutralSubText
            )
            if (dateTimeText.isNotBlank()) {
                Text(
                    text = dateTimeText,
                    fontFamily = NanumSquareRound,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = Color(0xFFAAB3C2)
                )
            }
        }
    }
}

@Composable
private fun RecordedVideoSection(
    accentColor: Color,
    videoUrl: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(196.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(VideoBackground)
            .clickable { /* TODO: 영상 재생 */ }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0xFF3D7EFF))
                .padding(horizontal = 12.dp, vertical = 5.dp)
        ) {
            Text(
                text = "녹화 영상",
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color.White
            )
        }
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.PlayCircle,
                contentDescription = "재생",
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(52.dp)
            )
            Text(
                text = "탭하여 영상 보기",
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
        Icon(
            imageVector = Icons.Outlined.Videocam,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.25f),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .size(22.dp)
        )
    }
}

@Composable
internal fun DetailSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(0.dp)) {
        Text(
            text = title,
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp,
            color = NeutralText,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(horizontal = 16.dp)
        ) {
            content()
        }
    }
}

@Composable
internal fun InfoRow(
    label: String,
    value: String,
    valueColor: Color = NeutralText,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = NeutralSubText
        )
        Text(
            text = value,
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = valueColor
        )
    }
}

@Composable
internal fun InfoDivider() {
    HorizontalDivider(color = Color(0xFFF0F2F6), thickness = 1.dp)
}

@Composable
internal fun ComponentStatusRow(
    name: String,
    isOnline: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = NeutralSubText
        )
        StatusChip(isOnline = isOnline)
    }
}

@Composable
internal fun StatusChip(isOnline: Boolean) {
    val chipColor = if (isOnline) Color(0xFF4CAF50) else DangerContent
    val label = if (isOnline) "온라인" else "오프라인"
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(chipColor.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = chipColor
        )
    }
}

@Composable
internal fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DangerContent)
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp,
            color = Color.White
        )
    }
}

@Composable
internal fun SecondaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = NeutralText
        )
    }
}
