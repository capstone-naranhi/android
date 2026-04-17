package com.example.android.ui.components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import com.example.android.ui.theme.BrandPrimaryLight
import com.example.android.ui.theme.NeutralUnselected

/** 하단 탭 메뉴 종류 */
enum class BottomNavigationItemType { HOME, LIVE, EVENTS }

/** 하단 탭 1개 정보 */
private data class BottomNavigationItem(
    val type: BottomNavigationItemType,
    val label: String,
    val icon: ImageVector
)

private val bottomNavigationItems = listOf(
    BottomNavigationItem(BottomNavigationItemType.HOME, "홈", Icons.Outlined.Home),
    BottomNavigationItem(BottomNavigationItemType.LIVE, "실시간", Icons.Outlined.PlayArrow),
    BottomNavigationItem(BottomNavigationItemType.EVENTS, "이벤트", Icons.Outlined.Event),
)

/**
 * 하단 탭 바 컴포넌트
 *
 * @param selectedItem      현재 선택된 탭
 * @param onItemSelected    탭 클릭 콜백
 * @param unreadEventCount  읽지 않은 모니터링 이벤트 수 (배지 표시용)
 */
@Composable
fun BottomNavigationBar(
    selectedItem: BottomNavigationItemType,
    onItemSelected: (BottomNavigationItemType) -> Unit,
    unreadEventCount: Int = 0,
    modifier: Modifier = Modifier
) {
    val selectedColor = MaterialTheme.colorScheme.primary

    NavigationBar(
        modifier = modifier.navigationBarsPadding(),
        containerColor = Color.White
    ) {
        bottomNavigationItems.forEach { item ->
            val showBadge = item.type == BottomNavigationItemType.EVENTS && unreadEventCount > 0

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
                                    Text(if (unreadEventCount > 99) "99+" else unreadEventCount.toString())
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
                    selectedIconColor = selectedColor,
                    selectedTextColor = selectedColor,
                    indicatorColor = BrandPrimaryLight,
                    unselectedIconColor = NeutralUnselected,
                    unselectedTextColor = NeutralUnselected
                )
            )
        }
    }
}
