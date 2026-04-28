package com.example.android

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.android.data.model.NotificationCategory
import com.example.android.ui.screens.DeviceDetailScreen
import com.example.android.ui.screens.DeviceNotificationDetailScreen
import com.example.android.ui.screens.HomeScreen
import com.example.android.ui.screens.LiveScreen
import com.example.android.ui.screens.MyPageScreen
import com.example.android.ui.screens.NotificationListScreen
import com.example.android.ui.screens.SafetyNotificationDetailScreen
import com.example.android.ui.screens.SettingScreen

object Routes {
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val MY_PAGE = "my_page"
    const val LIVE = "live"
    const val NOTIFICATIONS = "notifications"
    const val SAFETY_NOTIFICATION_DETAIL = "safety_notification_detail/{notificationId}"
    const val DEVICE_NOTIFICATION_DETAIL = "device_notification_detail/{notificationId}"
    const val DEVICE_DETAIL = "device_detail"

    fun safetyDetailRoute(id: String) = "safety_notification_detail/$id"
    fun deviceNotificationDetailRoute(id: String) = "device_notification_detail/$id"
}

/** 탭 간 이동 시 백스택이 쌓이지 않도록 HOME까지 popUpTo 처리 */
private fun navigateTab(navController: androidx.navigation.NavController, route: String) {
    navController.navigate(route) {
        popUpTo(Routes.HOME) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToSettings         = { navController.navigate(Routes.SETTINGS) },
                onNavigateToMyPage           = { navController.navigate(Routes.MY_PAGE) },
                onNavigateToLive             = { navigateTab(navController, Routes.LIVE) },
                onNavigateToNotificationList = { navigateTab(navController, Routes.NOTIFICATIONS) },
                onNavigateToSafetyDetail     = { id -> navController.navigate(Routes.safetyDetailRoute(id)) },
                onNavigateToDeviceDetail     = { navController.navigate(Routes.DEVICE_DETAIL) }
            )
        }

        composable(Routes.SETTINGS) {
            SettingScreen(
                onBack                  = { navController.popBackStack() },
                onNavigateToHome          = { navigateTab(navController, Routes.HOME) },
                onNavigateToLive          = { navigateTab(navController, Routes.LIVE) },
                onNavigateToNotifications = { navigateTab(navController, Routes.NOTIFICATIONS) }
            )
        }

        composable(Routes.MY_PAGE) {
            MyPageScreen(
                onBack               = { navController.popBackStack() },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(Routes.LIVE) {
            LiveScreen(
                onNavigateToHome          = { navigateTab(navController, Routes.HOME) },
                onNavigateToNotifications = { navigateTab(navController, Routes.NOTIFICATIONS) },
                onNavigateToSettings      = { navController.navigate(Routes.SETTINGS) },
                onNavigateToMyPage        = { navController.navigate(Routes.MY_PAGE) }
            )
        }

        composable(Routes.NOTIFICATIONS) {
            NotificationListScreen(
                onNavigateToHome      = { navigateTab(navController, Routes.HOME) },
                onNavigateToLive      = { navigateTab(navController, Routes.LIVE) },
                onNavigateToSettings  = { navController.navigate(Routes.SETTINGS) },
                onNavigateToMyPage    = { navController.navigate(Routes.MY_PAGE) },
                onNotificationClick   = { notification ->
                    when (notification.category) {
                        NotificationCategory.SAFETY_DANGER,
                        NotificationCategory.SAFETY_CAUTION,
                        NotificationCategory.SAFETY_INFO ->
                            navController.navigate(Routes.safetyDetailRoute(notification.id))
                        NotificationCategory.DEVICE ->
                            navController.navigate(Routes.deviceNotificationDetailRoute(notification.id))
                        else -> {}
                    }
                }
            )
        }

        composable(
            route = Routes.SAFETY_NOTIFICATION_DETAIL,
            arguments = listOf(navArgument("notificationId") { type = NavType.StringType })
        ) {
            SafetyNotificationDetailScreen(
                onBack       = { navController.popBackStack() },
                onConfirmNow = { navigateTab(navController, Routes.LIVE) }
            )
        }

        composable(
            route = Routes.DEVICE_NOTIFICATION_DETAIL,
            arguments = listOf(navArgument("notificationId") { type = NavType.StringType })
        ) {
            DeviceNotificationDetailScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.DEVICE_DETAIL) {
            DeviceDetailScreen(onBack = { navController.popBackStack() })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppNavigationPreview() {
    AppNavigation()
}