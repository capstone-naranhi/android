package com.example.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android.ui.components.BottomNavigationBar
import com.example.android.ui.components.BottomNavigationItemType
import com.example.android.ui.theme.AndroidTheme
import com.example.android.ui.theme.AppBackground
import com.example.android.ui.theme.BrandPrimary
import com.example.android.ui.theme.DangerContent
import com.example.android.ui.theme.DangerIconBg
import com.example.android.ui.theme.NanumSquareRound
import com.example.android.ui.theme.NeutralSubText
import com.example.android.ui.theme.NeutralText
import com.example.android.ui.theme.SuccessContent

// ─── 화면 ─────────────────────────────────────────────────────────────────────

@Composable
fun SettingScreen(onBack: () -> Unit = {}) {
    var deviceAlertOn by remember { mutableStateOf(true) }
    var reportAlertOn by remember { mutableStateOf(true) }
    var adAlertOn     by remember { mutableStateOf(false) }
    var videoSaveOn   by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = AppBackground,
        bottomBar = {
            BottomNavigationBar(
                selectedItem = BottomNavigationItemType.NOTIFICATIONS,
                onItemSelected = {}
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            Spacer(modifier = Modifier.statusBarsPadding())
            HorizontalDivider(color = Color(0xFFF7F9FC), thickness = 1.dp)

            Text(
                text = "설정",
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = NeutralText,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp,
                    top = 4.dp, bottom = 40.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                // ── 알림 ──────────────────────────────────────────────────────
                item { SettingSectionLabel("알림") }
                item {
                    SettingCardGroup {
                        AlwaysOnToggleRow(
                            icon     = Icons.Outlined.Warning,
                            iconBg   = Color(0xFFFFF3E0),
                            iconTint = Color(0xFFFF9800),
                            title    = "안전 알림",
                            subtitle = "질식·울음 등 위험·주의 감지"
                        )
                        SettingDivider()
                        SettingToggleRow(
                            icon            = Icons.Outlined.Videocam,
                            iconBg          = Color(0xFFF2F3F5),
                            iconTint        = Color(0xFF607D8B),
                            title           = "장치 알림",
                            subtitle        = "카메라·마이크 연결 상태",
                            checked         = deviceAlertOn,
                            onCheckedChange = { deviceAlertOn = it }
                        )
                        SettingDivider()
                        SettingToggleRow(
                            icon            = Icons.Outlined.BarChart,
                            iconBg          = Color(0xFFE8F5E9),
                            iconTint        = Color(0xFF43A047),
                            title           = "리포트 알림",
                            subtitle        = "매일 분석 리포트 수신",
                            checked         = reportAlertOn,
                            onCheckedChange = { reportAlertOn = it }
                        )
                        SettingDivider()
                        SettingToggleRow(
                            icon            = Icons.Outlined.Campaign,
                            iconBg          = Color(0xFFF2F3F5),
                            iconTint        = Color(0xFF9E9E9E),
                            title           = "광고·이벤트 알림",
                            subtitle        = "혜택·프로모션 안내",
                            checked         = adAlertOn,
                            onCheckedChange = { adAlertOn = it }
                        )
                        SettingDivider()
                        SettingChevronRow(
                            icon         = Icons.Outlined.Bedtime,
                            iconBg       = Color(0xFFFFFDE7),
                            iconTint     = Color(0xFFFFC107),
                            title        = "방해금지 시간",
                            subtitle     = "안전 알림은 항상 수신됩니다",
                            trailingText = "22:00-07:00",
                            onClick      = {}
                        )
                    }
                }

                // ── 모니터링 ──────────────────────────────────────────────────
                item { SettingSectionLabel("모니터링") }
                item {
                    SettingCardGroup {
                        SettingChevronRow(
                            icon         = Icons.Outlined.TrackChanges,
                            iconBg       = Color(0xFFFFEBEE),
                            iconTint     = DangerContent,
                            title        = "감지 민감도",
                            subtitle     = "울음·움직임 감지 기준",
                            trailingText = "보통",
                            onClick      = {}
                        )
                        SettingDivider()
                        SettingToggleRow(
                            icon            = Icons.Outlined.Movie,
                            iconBg          = Color(0xFF1C2030),
                            iconTint        = Color(0xFFB0BEC5),
                            title           = "이벤트 영상 저장",
                            subtitle        = "위험·주의 감지 시 자동 저장",
                            checked         = videoSaveOn,
                            onCheckedChange = { videoSaveOn = it }
                        )
                        SettingDivider()
                        SettingChevronRow(
                            icon         = Icons.Outlined.Inventory2,
                            iconBg       = Color(0xFFFFF3E0),
                            iconTint     = Color(0xFFFF9800),
                            title        = "영상 보관 기간",
                            subtitle     = null,
                            trailingText = "30일",
                            onClick      = {}
                        )
                    }
                }

                // ── 계정 ──────────────────────────────────────────────────────
                item { SettingSectionLabel("계정") }
                item {
                    SettingCardGroup {
                        SettingChevronRow(
                            icon         = Icons.Outlined.Lock,
                            iconBg       = Color(0xFFF2F3F5),
                            iconTint     = NeutralSubText,
                            title        = "비밀번호 변경",
                            subtitle     = null,
                            trailingText = null,
                            onClick      = {}
                        )
                        SettingDivider()
                        SettingChevronRow(
                            icon         = Icons.Outlined.FileUpload,
                            iconBg       = Color(0xFFF2F3F5),
                            iconTint     = NeutralSubText,
                            title        = "데이터 내보내기",
                            subtitle     = "알림 이력·리포트 다운로드",
                            trailingText = null,
                            onClick      = {}
                        )
                        SettingDivider()
                        SettingDeleteRow(onClick = {})
                    }
                }

                // ── 앱 정보 ───────────────────────────────────────────────────
                item {
                    Text(
                        text     = "v1.0.0 · 앱 정보",
                        fontFamily  = NanumSquareRound,
                        fontWeight  = FontWeight.Normal,
                        fontSize    = 12.sp,
                        color       = NeutralSubText,
                        textAlign   = TextAlign.Center,
                        modifier    = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    )
                }
            }
        }
    }
}

// ─── Section Label ────────────────────────────────────────────────────────────

@Composable
private fun SettingSectionLabel(title: String, modifier: Modifier = Modifier) {
    Text(
        text       = title,
        fontFamily = NanumSquareRound,
        fontWeight = FontWeight.Bold,
        fontSize   = 13.sp,
        color      = NeutralSubText,
        modifier   = modifier.padding(horizontal = 4.dp, vertical = 4.dp)
    )
}

// ─── Card Group ───────────────────────────────────────────────────────────────

@Composable
private fun SettingCardGroup(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White),
        content = content
    )
}

// ─── Divider ──────────────────────────────────────────────────────────────────

@Composable
private fun SettingDivider() {
    HorizontalDivider(
        color    = Color(0xFFF0F2F5),
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

// ─── Icon Box ─────────────────────────────────────────────────────────────────

@Composable
private fun SettingIconBox(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(iconBg),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector     = icon,
            contentDescription = null,
            tint            = iconTint,
            modifier        = Modifier.size(20.dp)
        )
    }
}

// ─── Always-On Toggle Row (안전 알림) ─────────────────────────────────────────

@Composable
private fun AlwaysOnToggleRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SettingIconBox(icon = icon, iconBg = iconBg, iconTint = iconTint)

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text       = title,
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Bold,
                fontSize   = 14.sp,
                color      = NeutralText
            )
            Text(
                text       = subtitle,
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Normal,
                fontSize   = 12.sp,
                color      = NeutralSubText
            )
        }

        // 항상 켜짐 배지
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(SuccessContent.copy(alpha = 0.12f))
                .padding(horizontal = 9.dp, vertical = 4.dp)
        ) {
            Text(
                text       = "항상 켜짐",
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Bold,
                fontSize   = 11.sp,
                color      = SuccessContent
            )
        }

        Switch(
            checked         = true,
            onCheckedChange = null   // 비활성 인터랙션 (항상 켜짐)
        )
    }
}

// ─── Toggle Row ───────────────────────────────────────────────────────────────

@Composable
private fun SettingToggleRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SettingIconBox(icon = icon, iconBg = iconBg, iconTint = iconTint)

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text       = title,
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Bold,
                fontSize   = 14.sp,
                color      = NeutralText
            )
            Text(
                text       = subtitle,
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Normal,
                fontSize   = 12.sp,
                color      = NeutralSubText
            )
        }

        Switch(
            checked         = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor   = BrandPrimary,
                checkedThumbColor   = Color.White,
                uncheckedTrackColor = Color(0xFFDDE1E7),
                uncheckedThumbColor = Color.White,
                uncheckedBorderColor = Color(0xFFDDE1E7)
            )
        )
    }
}

// ─── Chevron Row ──────────────────────────────────────────────────────────────

@Composable
private fun SettingChevronRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String?,
    trailingText: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SettingIconBox(icon = icon, iconBg = iconBg, iconTint = iconTint)

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text       = title,
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Bold,
                fontSize   = 14.sp,
                color      = NeutralText
            )
            if (subtitle != null) {
                Text(
                    text       = subtitle,
                    fontFamily = NanumSquareRound,
                    fontWeight = FontWeight.Normal,
                    fontSize   = 12.sp,
                    color      = NeutralSubText
                )
            }
        }

        if (trailingText != null) {
            Text(
                text       = trailingText,
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Bold,
                fontSize   = 13.sp,
                color      = NeutralSubText
            )
        }

        Icon(
            imageVector     = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint            = Color(0xFFB8C0CC),
            modifier        = Modifier.size(18.dp)
        )
    }
}

// ─── Delete Row ───────────────────────────────────────────────────────────────

@Composable
private fun SettingDeleteRow(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(DangerIconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector     = Icons.Outlined.Delete,
                contentDescription = null,
                tint            = DangerContent,
                modifier        = Modifier.size(20.dp)
            )
        }
        Text(
            text       = "계정 삭제",
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Bold,
            fontSize   = 14.sp,
            color      = DangerContent
        )
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingScreenPreview() {
    AndroidTheme {
        SettingScreen()
    }
}
