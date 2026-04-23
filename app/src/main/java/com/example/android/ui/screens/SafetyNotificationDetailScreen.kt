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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.android.data.model.DeviceComponentStatus
import com.example.android.data.model.NotificationCategory
import com.example.android.data.model.NotificationSeverity
import com.example.android.data.model.SafetyNotificationDetail
import com.example.android.ui.theme.AndroidTheme
import com.example.android.ui.theme.AppBackground
import com.example.android.ui.theme.DangerContent
import com.example.android.ui.theme.NanumSquareRound
import com.example.android.ui.theme.NeutralSubText
import com.example.android.ui.theme.NeutralText
import com.example.android.ui.theme.StatusOnline
import com.example.android.ui.theme.VideoBackground
import com.example.android.ui.theme.WarningContent

// ─── 샘플 데이터 ──────────────────────────────────────────────────────────────

private val sampleSafetyDetail = SafetyNotificationDetail(
    id = "1",
    category = NotificationCategory.SAFETY_DANGER,
    badgeLabel = "위험",
    title = "질식 위험 감지됨",
    cameraName = "아기방 카메라",
    dateTimeText = "2026. 04. 20 · 오전 10:32",
    hasRecording = true,
    recordingDurationText = "00:08",
    eventType = "질식 위험",
    severity = "위험 (DANGER)",
    severityLevel = NotificationSeverity.DANGER,
    durationText = "8초",
    camera = "아기방",
    deviceComponents = listOf(
        DeviceComponentStatus("카메라", true),
        DeviceComponentStatus("마이크", true),
        DeviceComponentStatus("보드(Jetson)", true),
    )
)

// ─── 화면 ─────────────────────────────────────────────────────────────────────

@Composable
fun SafetyNotificationDetailScreen(
    detail: SafetyNotificationDetail = sampleSafetyDetail,
    onBack: () -> Unit = {},
    onConfirmNow: () -> Unit = {}
) {
    val accentColor = when (detail.category) {
        NotificationCategory.SAFETY_DANGER  -> DangerContent
        NotificationCategory.SAFETY_CAUTION -> WarningContent
        else                                -> Color(0xFF3D7EFF)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .statusBarsPadding()
    ) {
        HorizontalDivider(color = Color(0xFFE5E9F0), thickness = 1.dp)
        // ── 상단 바 ───────────────────────────────────────────────────────────
        DetailTopBar(title = "알림", onBack = onBack)
        HorizontalDivider(color = Color(0xFFE5E9F0), thickness = 1.dp)

        // ── 스크롤 콘텐츠 ─────────────────────────────────────────────────────
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
                badgeLabel = detail.badgeLabel,
                title = detail.title,
                subtitle = detail.cameraName,
                dateTimeText = detail.dateTimeText
            )

            // 녹화 영상
            if (detail.hasRecording) {
                RecordedVideoSection(
                    accentColor = accentColor,
                    durationText = detail.recordingDurationText
                )
            }

            // 감지 정보
            DetailSection(title = "감지 정보") {
                InfoRow(label = "이벤트 유형", value = detail.eventType)
                InfoDivider()
                InfoRow(
                    label = "심각도",
                    value = detail.severity,
                    valueColor = when (detail.severityLevel) {
                        NotificationSeverity.DANGER  -> DangerContent
                        NotificationSeverity.WARNING -> WarningContent
                        NotificationSeverity.INFO    -> Color(0xFF3D7EFF)
                    }
                )
                InfoDivider()
                InfoRow(label = "지속 시간", value = detail.durationText)
                InfoDivider()
                InfoRow(label = "카메라", value = detail.camera)
            }

            // 감지 당시 장치 상태
            if (detail.deviceComponents.isNotEmpty()) {
                DetailSection(title = "감지 당시 장치 상태") {
                    detail.deviceComponents.forEachIndexed { index, component ->
                        ComponentStatusRow(
                            name = component.name,
                            isOnline = component.isOnline
                        )
                        if (index < detail.deviceComponents.lastIndex) {
                            InfoDivider()
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }

        // ── 하단 버튼 ─────────────────────────────────────────────────────────
        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            PrimaryActionButton(
                text = "지금 바로 확인하기",
                onClick = onConfirmNow
            )
        }
    }
}

// ─── 공통 서브 컴포넌트 ────────────────────────────────────────────────────────

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
        // 좌측 컬러 바
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
            // 배지
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

@Composable
private fun RecordedVideoSection(
    accentColor: Color,
    durationText: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(196.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(VideoBackground)
            .clickable { /* 녹화 재생 */ }
    ) {
        // 녹화됨 배지 (녹화 영상은 항상 초록색 고정)
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0xFF3D7EFF))
                .padding(horizontal = 12.dp, vertical = 5.dp)
        ) {
            Text(
                text = "녹화됨",
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color.White
            )
        }

        // 재생 버튼 + 영상 길이
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
                text = "영상 재생 (${durationText})",
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }

        // 카메라 아이콘 (우하단)
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
    val chipColor = if (isOnline) StatusOnline else DangerContent
    val label = if (isOnline) "ONLINE" else "OFFLINE"
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
            .background(Color(0xFF7A1A1A))
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

// ─── 프리뷰 ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SafetyNotificationDetailScreenPreview() {
    AndroidTheme {
        SafetyNotificationDetailScreen()
    }
}
