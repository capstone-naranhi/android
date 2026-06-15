package com.example.android.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.android.ui.theme.BrandPrimary
import com.example.android.ui.theme.BrandPrimaryLight
import com.example.android.ui.theme.NeutralSubText
import com.example.android.ui.theme.NeutralSurface

/** 하단 탭 메뉴 종류 */
enum class BottomNavigationItemType { HOME, LIVE, NOTIFICATIONS }

/** 하단 탭 1개 정보 */
private data class BottomNavigationItem(
    val type: BottomNavigationItemType,
    val label: String,
    val icon: ImageVector
)

private val bottomNavigationItems = listOf(
    BottomNavigationItem(BottomNavigationItemType.HOME,          "홈",    Icons.Outlined.Home),
    BottomNavigationItem(BottomNavigationItemType.LIVE,          "실시간", Icons.Outlined.PlayCircle),
    BottomNavigationItem(BottomNavigationItemType.NOTIFICATIONS, "알림",  Icons.Outlined.Notifications),
)

/**
 * 하단 탭 바 컴포넌트
 *
 * @param selectedItem              현재 선택된 탭
 * @param onItemSelected            탭 클릭 콜백
 * @param unreadNotificationCount   읽지 않은 알림 수 (배지 표시용)
 */
@Composable
fun BottomNavigationBar(
    selectedItem: BottomNavigationItemType?,
    onItemSelected: (BottomNavigationItemType) -> Unit,
    unreadNotificationCount: Int = 0,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        HorizontalDivider(color = NeutralSurface, thickness = 1.dp)
        NavigationBar(
            modifier = Modifier
                .navigationBarsPadding()
                .height(60.dp),
            containerColor = Color.White
        ) {
            bottomNavigationItems.forEach { item ->
                val showBadge = item.type == BottomNavigationItemType.NOTIFICATIONS && unreadNotificationCount > 0

                NavigationBarItem(
                    selected = selectedItem == item.type,
                    onClick = { onItemSelected(item.type) },
                    icon = {
                        val iconComposable: @Composable () -> Unit = {
                            Icon(imageVector = item.icon, contentDescription = item.label)
                        }
                        if (showBadge) {
                            BadgedBox(
                                badge = {
                                    Badge {
                                        Text(if (unreadNotificationCount > 99) "99+" else unreadNotificationCount.toString())
                                    }
                                }
                            ) { iconComposable() }
                        } else {
                            iconComposable()
                        }
                    },
                    label = {
                        Text(text = item.label, style = MaterialTheme.typography.labelLarge)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandPrimary,
                        selectedTextColor = BrandPrimary,
                        indicatorColor = BrandPrimaryLight,
                        unselectedIconColor = NeutralSubText,
                        unselectedTextColor = NeutralSubText
                    )
                )
            }
        }
    }
}
