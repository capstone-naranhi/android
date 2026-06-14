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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBackIos
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import com.example.android.data.model.DeviceListItemData
import com.example.android.ui.components.BottomNavigationBar
import com.example.android.ui.components.BottomNavigationItemType
import com.example.android.ui.theme.AppBackground
import com.example.android.ui.theme.BrandPrimary
import com.example.android.ui.theme.DangerContent
import com.example.android.ui.theme.InfoContent
import com.example.android.ui.theme.NanumSquareRound
import com.example.android.ui.theme.NeutralSubText
import com.example.android.ui.theme.NeutralText
import com.example.android.ui.theme.StatusOnline

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun DeviceListScreen(
    onBack: () -> Unit = {},
    onNavigateToDeviceDetail: (Long) -> Unit = {},
    viewModel: DeviceListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = AppBackground,
        bottomBar = {
            BottomNavigationBar(
                selectedItem = BottomNavigationItemType.HOME,
                onItemSelected = {}
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            Spacer(Modifier.statusBarsPadding())
            DeviceListTopBar(onBack = onBack)
            HorizontalDivider(color = Color(0xFFF0F2F5))

            when (val state = uiState) {
                is DeviceListUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BrandPrimary)
                    }
                }

                is DeviceListUiState.Error -> {
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

                is DeviceListUiState.Success -> {
                    if (state.devices.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "등록된 장치가 없습니다.\n설정에서 장치를 등록해주세요.",
                                fontFamily = NanumSquareRound,
                                fontSize = 14.sp,
                                color = NeutralSubText,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(
                                start = 16.dp, end = 16.dp,
                                top = 16.dp, bottom = 32.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.devices) { device ->
                                DeviceListCard(
                                    device = device,
                                    onClick = { onNavigateToDeviceDetail(device.deviceId) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Top Bar ─────────────────────────────────────────────────────────────────

@Composable
private fun DeviceListTopBar(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable { onBack() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBackIos,
                contentDescription = "뒤로",
                tint = InfoContent,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "뒤로",
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = InfoContent
            )
        }

        Text(
            text = "기기 목록",
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 17.sp,
            color = NeutralText,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

// ─── Device List Card ─────────────────────────────────────────────────────────

@Composable
private fun DeviceListCard(
    device: DeviceListItemData,
    onClick: () -> Unit
) {
    val isOnline = device.heartbeatStatus?.uppercase() == "ONLINE"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 상태 점
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    color = if (isOnline) StatusOnline else Color(0xFFDDE1E7),
                    shape = CircleShape
                )
        )

        // 장치 정보
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = device.deviceName,
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = NeutralText
            )
            Text(
                text = device.locationName,
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = NeutralSubText
            )

            // 컴포넌트 상태 뱃지 행
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ComponentStatusBadge(
                    icon = Icons.Outlined.Videocam,
                    label = "카메라",
                    isOnline = device.cameraStatus?.uppercase() == "ONLINE"
                )
                ComponentStatusBadge(
                    icon = Icons.Outlined.Mic,
                    label = "마이크",
                    isOnline = device.micStatus?.uppercase() == "ONLINE"
                )
                ComponentStatusBadge(
                    icon = Icons.Outlined.Computer,
                    label = "보드",
                    isOnline = device.boardStatus?.uppercase() == "ONLINE"
                )
            }
        }

        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = Color(0xFFB8C0CC),
            modifier = Modifier.size(18.dp)
        )
    }
}

// ─── Component Status Badge ───────────────────────────────────────────────────

@Composable
private fun ComponentStatusBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isOnline: Boolean
) {
    val tint = if (isOnline) StatusOnline else DangerContent
    val bg   = if (isOnline) StatusOnline.copy(alpha = 0.10f) else DangerContent.copy(alpha = 0.10f)

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(bg)
            .padding(horizontal = 7.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(11.dp)
        )
        Text(
            text = label,
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            color = tint
        )
    }
}
