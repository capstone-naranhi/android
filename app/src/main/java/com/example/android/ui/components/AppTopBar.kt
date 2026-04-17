package com.example.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.android.ui.theme.Dimens

/**
 * 공통 상단 바 컴포넌트
 *
 * @param title                     화면의 큰 제목
 * @param subtitle                  제목 아래 들어갈 작은 설명 문구
 * @param subtitleBadgeColor        subtitle 앞에 표시할 상태 배지 색상 (null이면 미표시)
 * @param showBackButton            뒤로가기 버튼 표시 여부
 * @param showProfileButton         프로필 버튼 표시 여부
 * @param showRefreshButton         새로고침 버튼 표시 여부
 * @param showNotificationButton    알림 버튼 표시 여부
 * @param unreadNotificationCount   읽지 않은 알림 수 (배지 표시용)
 * @param onBackClick               뒤로가기 버튼 콜백
 * @param onProfileClick            프로필 버튼 콜백
 * @param onRefreshClick            새로고침 버튼 콜백
 * @param onNotificationClick       알림 버튼 콜백
 */
@Composable
fun TopAppBar(
    title: String,
    subtitle: String? = null,
    subtitleBadgeColor: Color? = null,
    showBackButton: Boolean = false,
    showProfileButton: Boolean = false,
    showRefreshButton: Boolean = false,
    showNotificationButton: Boolean = false,
    unreadNotificationCount: Int = 0,
    onBackClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onRefreshClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val barColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = barColor,
                shape = RoundedCornerShape(
                    bottomStart = Dimens.radiusPage,
                    bottomEnd = Dimens.radiusPage
                )
            )
            .padding(start = Dimens.spaceXl, end = Dimens.spaceXl, top = 30.dp, bottom = 40.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // 왼쪽: 뒤로가기 + 제목
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (showBackButton) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "뒤로가기",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.size(Dimens.spaceXs))
                    }

                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White
                        )
                        if (subtitle != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (subtitleBadgeColor != null) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(
                                                color = subtitleBadgeColor,
                                                shape = CircleShape
                                            )
                                    )
                                    Spacer(modifier = Modifier.size(Dimens.spaceS))
                                }
                                Text(
                                    text = subtitle,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White.copy(alpha = 0.92f)
                                )
                            }
                        }
                    }
                }

                // 오른쪽: 액션 아이콘들
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (showNotificationButton) {
                        ActionIconBox(onClick = onNotificationClick) {
                            // 읽지 않은 알림가 있으면 배지 표시
                            if (unreadNotificationCount > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge {
                                            Text(
                                                if (unreadNotificationCount > 99) "99+"
                                                else unreadNotificationCount.toString()
                                            )
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Notifications,
                                        contentDescription = "알림",
                                        tint = Color.White
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.Notifications,
                                    contentDescription = "알림",
                                    tint = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.size(Dimens.spaceS))
                    }

                    if (showProfileButton) {
                        ActionIconBox(
                            imageVector = Icons.Outlined.AccountCircle,
                            contentDescription = "프로필",
                            onClick = onProfileClick
                        )
                    }

                    if (showRefreshButton) {
                        Spacer(modifier = Modifier.size(Dimens.spaceS))
                        ActionIconBox(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "새로고침",
                            onClick = onRefreshClick
                        )
                    }
                }
            }
        }
    }
}

/**
 * 작은 액션 아이콘 박스 — 아이콘을 직접 넘기는 단순 버전
 */
@Composable
private fun ActionIconBox(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    ActionIconBox(onClick = onClick) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = Color.White
        )
    }
}

/**
 * 작은 액션 아이콘 박스 — 배지 등 커스텀 content가 필요할 때 쓰는 슬롯 버전
 */
@Composable
private fun ActionIconBox(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(Dimens.avatarM)
            .background(
                color = Color.White.copy(alpha = 0.12f),
                shape = RoundedCornerShape(Dimens.radiusM)
            ),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick) {
            content()
        }
    }
}