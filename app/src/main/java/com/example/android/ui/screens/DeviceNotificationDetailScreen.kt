package com.example.android.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.example.android.data.model.DeviceNotificationDetail
import com.example.android.ui.theme.AndroidTheme
import com.example.android.ui.theme.AppBackground
import com.example.android.ui.theme.DangerContent
import com.example.android.ui.theme.NanumSquareRound
import com.example.android.ui.theme.NeutralSubText
import com.example.android.ui.theme.NeutralText
import com.example.android.ui.theme.StatusOnline
import com.example.android.ui.theme.WarningContent

// ─── 샘플 데이터 ──────────────────────────────────────────────────────────────

private val sampleDeviceDetail = DeviceNotificationDetail(
    id = "3",
    badgeLabel = "장치",
    title = "거실 카메라 연결 끊김",
    deviceDescription = "거실 카메라",
    dateTimeText = "2026. 04. 20 · 오전 9:14",
    component = "카메라",
    previousStatus = "ONLINE",
    currentStatus = "OFFLINE",
    deviceName = "거실",
    reason = "네트워크 연결 끊김",
    componentStatuses = listOf(
        DeviceComponentStatus("카메라", false),
        DeviceComponentStatus("마이크", false),
        DeviceComponentStatus("보드(Jetson)", false),
    ),
    lastHeartbeat = "09:14:02"
)

// ─── 화면 ─────────────────────────────────────────────────────────────────────

@Composable
fun DeviceNotificationDetailScreen(
    detail: DeviceNotificationDetail = sampleDeviceDetail,
    onBack: () -> Unit = {},
    onGoToDeviceStatus: () -> Unit = {},
    onContactSupport: () -> Unit = {}
) {
    val accentColor = Color(0xFF8B96AA) // 장치 알림 고정 색상

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
                subtitle = detail.deviceDescription,
                dateTimeText = detail.dateTimeText
            )

            // 장치 변경 정보
            DetailSection(title = "장치 변경 정보") {
                InfoRow(label = "컴포넌트", value = detail.component)
                InfoDivider()
                StatusChangeRow(
                    previous = detail.previousStatus,
                    current = detail.currentStatus
                )
                InfoDivider()
                InfoRow(label = "장치 이름", value = detail.deviceName)
                InfoDivider()
                InfoRow(
                    label = "사유",
                    value = detail.reason,
                    valueColor = WarningContent
                )
            }

            // 동일 장치 컴포넌트 현황
            DetailSection(title = "동일 장치 컴포넌트 현황") {
                detail.componentStatuses.forEachIndexed { index, component ->
                    ComponentStatusRow(
                        name = component.name,
                        isOnline = component.isOnline
                    )
                    InfoDivider()
                }
                // 마지막 하트비트
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "마지막 하트비트",
                        fontFamily = NanumSquareRound,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = NeutralSubText
                    )
                    Text(
                        text = detail.lastHeartbeat,
                        fontFamily = NanumSquareRound,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = NeutralText
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }

        // ── 하단 버튼 영역 ────────────────────────────────────────────────────
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PrimaryActionButton(
                text = "장치 상태 화면으로 이동",
                onClick = onGoToDeviceStatus
            )
            SecondaryActionButton(
                text = "지원 문의하기",
                onClick = onContactSupport
            )
        }
    }
}

// ─── 상태 변경 행 (ONLINE → OFFLINE 표시) ─────────────────────────────────────

@Composable
private fun StatusChangeRow(
    previous: String,
    current: String,
    modifier: Modifier = Modifier
) {
    val prevIsOnline = previous.uppercase() == "ONLINE"
    val curIsOnline = current.uppercase() == "ONLINE"
    val prevColor = if (prevIsOnline) StatusOnline else DangerContent
    val curColor = if (curIsOnline) StatusOnline else DangerContent

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "상태 변경",
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = NeutralSubText
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            StatusPill(label = previous, color = prevColor)
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = null,
                tint = NeutralSubText,
                modifier = Modifier.size(16.dp)
            )
            StatusPill(label = current, color = curColor)
        }
    }
}

@Composable
private fun StatusPill(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = color
        )
    }
}

// ─── 프리뷰 ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DeviceNotificationDetailScreenPreview() {
    AndroidTheme {
        DeviceNotificationDetailScreen()
    }
}
