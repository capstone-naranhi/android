package com.example.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android.ui.theme.AndroidTheme
import com.example.android.ui.theme.AppBackground
import com.example.android.ui.theme.DangerContent
import com.example.android.ui.theme.InfoContent
import com.example.android.ui.theme.NanumSquareRound
import com.example.android.ui.theme.NeutralSubText
import com.example.android.ui.theme.NeutralSurface
import com.example.android.ui.theme.NeutralText

// ─── 설정 행 데이터 ───────────────────────────────────────────────────────────

private data class SettingsRow(
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color,
    val title: String,
    val subtitle: String,
    val badge: String? = null,
    val badgeColor: Color = DangerContent
)

// ─── 화면 ─────────────────────────────────────────────────────────────────────

@Composable
fun MyPageScreen(onBack: () -> Unit = {}) {
    Scaffold(containerColor = AppBackground) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            Spacer(modifier = Modifier.statusBarsPadding())
            HorizontalDivider(color = NeutralSurface, thickness = 1.dp)

            // 타이틀 행
            MyPageTitleRow(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )

            HorizontalDivider(color = NeutralSurface, thickness = 1.dp)

            LazyColumn(
                contentPadding = PaddingValues(
                    start = 20.dp, end = 20.dp,
                    top = 20.dp, bottom = 40.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 프로필 카드
                item { ProfileCard() }

                // 아이 정보
                item {
                    SectionHeader(
                        title = "아이 정보",
                        actionText = "+ 추가",
                        onAction = {}
                    )
                }
                item { ChildCard() }

                // 기기 관리
                item { SectionHeader(title = "기기 관리") }
                item {
                    SettingsGroup(
                        rows = listOf(
                            SettingsRow(
                                icon = Icons.Outlined.Videocam,
                                iconBg = NeutralSurface,
                                iconTint = NeutralSubText,
                                title = "연결된 카메라",
                                subtitle = "1대 등록 · 1대 연결 중",
                                badge = "모두 1",
                                badgeColor = DangerContent
                            ),
                            SettingsRow(
                                icon = Icons.Outlined.Notifications,
                                iconBg = NeutralSurface,
                                iconTint = NeutralSubText,
                                title = "알림 설정",
                                subtitle = "알림 유형 · 방해금지 시간"
                            )
                        )
                    )
                }

                // 지원
                item { SectionHeader(title = "지원") }
                item {
                    SettingsGroup(
                        rows = listOf(
                            SettingsRow(
                                icon = Icons.Outlined.Description,
                                iconBg = NeutralSurface,
                                iconTint = NeutralSubText,
                                title = "이용약관 · 개인정보처리방침",
                                subtitle = ""
                            ),
                            SettingsRow(
                                icon = Icons.AutoMirrored.Outlined.Chat,
                                iconBg = NeutralSurface,
                                iconTint = NeutralSubText,
                                title = "고객센터",
                                subtitle = ""
                            ),
                            SettingsRow(
                                icon = Icons.Outlined.Star,
                                iconBg = NeutralSurface,
                                iconTint = NeutralSubText,
                                title = "앱 평가하기",
                                subtitle = ""
                            )
                        )
                    )
                }

                // 로그아웃
                item { Spacer(modifier = Modifier.height(8.dp)) }
                item { LogoutButton(onClick = {}) }
                item {
                    Text(
                        text = "v1.0.0",
                        fontFamily = NanumSquareRound,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = NeutralSubText,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

// ─── 타이틀 행 ────────────────────────────────────────────────────────────────

@Composable
private fun MyPageTitleRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "마이페이지",
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            color = NeutralText
        )
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(NeutralSurface),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "프로필",
                tint = NeutralSubText,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ─── 프로필 카드 ──────────────────────────────────────────────────────────────

@Composable
private fun ProfileCard(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 이니셜 아바타
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF2E4A7A)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "김",
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = Color.White
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = "김지민",
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = NeutralText
            )
            Text(
                text = "jimin@email.com",
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = NeutralSubText
            )
        }

        Text(
            text = "편집",
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = InfoContent,
            modifier = Modifier.clickable {}
        )
    }
}

// ─── 아이 정보 카드 ───────────────────────────────────────────────────────────

@Composable
private fun ChildCard(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .clickable {}
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFF3E0)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🐣", fontSize = 22.sp)
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = "김하준",
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = NeutralText
            )
            Text(
                text = "생후 8개월 · 2024년 8월 12일생",
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = NeutralSubText
            )
        }

        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = NeutralSubText,
            modifier = Modifier.size(20.dp)
        )
    }
}

// ─── 설정 그룹 ────────────────────────────────────────────────────────────────

@Composable
private fun SettingsGroup(rows: List<SettingsRow>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
    ) {
        rows.forEachIndexed { index, row ->
            SettingsRowItem(row = row)
            if (index != rows.lastIndex) {
                HorizontalDivider(
                    color = NeutralSurface,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingsRowItem(row: SettingsRow, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {}
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(row.iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = row.icon,
                contentDescription = null,
                tint = row.iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = row.title,
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = NeutralText
            )
            if (row.subtitle.isNotEmpty()) {
                Text(
                    text = row.subtitle,
                    fontFamily = NanumSquareRound,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = NeutralSubText
                )
            }
        }

        if (row.badge != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(row.badgeColor.copy(alpha = 0.15f))
                    .padding(horizontal = 9.dp, vertical = 4.dp)
            ) {
                Text(
                    text = row.badge,
                    fontFamily = NanumSquareRound,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = row.badgeColor
                )
            }
        }

        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = NeutralSubText,
            modifier = Modifier.size(18.dp)
        )
    }
}

// ─── 섹션 헤더 ────────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(
    title: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = NeutralSubText
        )
        if (actionText != null) {
            Text(
                text = actionText,
                fontFamily = NanumSquareRound,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = InfoContent,
                modifier = Modifier.clickable { onAction?.invoke() }
            )
        }
    }
}

// ─── 로그아웃 버튼 ────────────────────────────────────────────────────────────

@Composable
private fun LogoutButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = 1.dp,
                color = NeutralSurface,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "로그아웃",
            fontFamily = NanumSquareRound,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = NeutralSubText
        )
    }
}

// ─── 프리뷰 ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyPageScreenPreview() {
    AndroidTheme {
        MyPageScreen()
    }
}
