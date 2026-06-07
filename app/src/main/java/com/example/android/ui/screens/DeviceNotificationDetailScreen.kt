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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.example.android.data.model.toComponentLabel
import com.example.android.data.model.toKoreanDateTimeString
import com.example.android.data.model.toStatusLabel
import com.example.android.ui.theme.AppBackground
import com.example.android.ui.theme.BrandPrimary
import com.example.android.ui.theme.DangerContent
import com.example.android.ui.theme.NanumSquareRound
import com.example.android.ui.theme.NeutralSubText
import com.example.android.ui.theme.NeutralText
import com.example.android.ui.theme.StatusOnline
import com.example.android.ui.theme.WarningContent

// ─── 화면 ─────────────────────────────────────────────────────────────────────

@Composable
fun DeviceNotificationDetailScreen(
    notificationId: String,
    onBack: () -> Unit = {},
    onGoToDeviceStatus: () -> Unit = {},
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
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
                DeviceDetailContent(
                    modifier = Modifier.weight(1f),
                    data = state.data,
                    onGoToDeviceStatus = onGoToDeviceStatus
                )
            }
        }
    }
}

// ─── 콘텐츠 ───────────────────────────────────────────────────────────────────

@Composable
private fun DeviceDetailContent(
    data: NotificationDetailData,
    onGoToDeviceStatus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val device = data.deviceDetail
    val accentColor = Color(0xFF8B96AA) // 장치 알림 고정 색상

    val isOffline = device?.currentStatus?.uppercase() != "ONLINE"
    val titleText = device?.deviceName?.let {
        if (isOffline) "$it 연결 끊김" else "$it 연결됨"
    } ?: "기기 상태 변경"
    val componentLabel = device?.componentType.toComponentLabel()
    val beforeLabel = device?.beforeStatus.toStatusLabel()
    val currentLabel = device?.currentStatus.toStatusLabel()
    val dateTimeText = data.sentAt.toKoreanDateTimeString()

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
                badgeLabel = "기기",
                title = titleText,
                subtitle = componentLabel,
                dateTimeText = dateTimeText
            )

            // 장치 변경 정보
            DetailSection(title = "장치 변경 정보") {
                InfoRow(label = "컴포넌트", value = componentLabel)
                InfoDivider()
                StatusChangeRow(
                    previous = device?.beforeStatus ?: "-",
                    previousLabel = beforeLabel,
                    current = device?.currentStatus ?: "-",
                    currentLabel = currentLabel
                )
                InfoDivider()
                InfoRow(label = "기기 이름", value = device?.deviceName ?: "-")
                if (!device?.description.isNullOrBlank()) {
                    InfoDivider()
                    InfoRow(
                        label = "사유",
                        value = device?.description ?: "-",
                        valueColor = WarningContent
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }

        // 하단 버튼
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PrimaryActionButton(
                text = "장치 상태 화면으로 이동",
                onClick = onGoToDeviceStatus
            )
        }
    }
}

// ─── 상태 변경 행 ─────────────────────────────────────────────────────────────

@Composable
private fun StatusChangeRow(
    previous: String,
    previousLabel: String,
    current: String,
    currentLabel: String,
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
            StatusPill(label = previousLabel, color = prevColor)
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = null,
                tint = NeutralSubText,
                modifier = Modifier.size(16.dp)
            )
            StatusPill(label = currentLabel, color = curColor)
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
