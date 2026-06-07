package com.example.android

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.activity.compose.LocalActivity
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.android.data.model.NotificationCategory
import com.example.android.data.network.SessionManager
import com.example.android.data.network.SessionRepository
import com.example.android.fcm.IbomMessagingService.Companion.EXTRA_NOTIF_ID
import com.example.android.fcm.IbomMessagingService.Companion.EXTRA_SCREEN
import com.example.android.fcm.IbomMessagingService.Companion.EXTRA_TYPE
import com.example.android.fcm.IbomMessagingService.Companion.TYPE_DEVICE
import com.example.android.fcm.IbomMessagingService.Companion.TYPE_SAFETY
import com.example.android.ui.screens.DeviceDetailScreen
import com.example.android.ui.screens.DeviceNotificationDetailScreen
import com.example.android.ui.screens.HomeScreen
import com.example.android.ui.screens.LiveScreen
import com.example.android.ui.screens.LoginScreen
import com.example.android.ui.screens.MyPageScreen
import com.example.android.ui.screens.NotificationListScreen
import com.example.android.ui.screens.SafetyNotificationDetailScreen
import com.example.android.ui.screens.SettingScreen
import kotlinx.coroutines.launch

object Routes {
    const val LOGIN = "login"
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

/** 로그인 화면으로 이동하며 백스택 전체를 초기화합니다. */
private fun navigateToLogin(navController: androidx.navigation.NavController) {
    navController.navigate(Routes.LOGIN) {
        popUpTo(0) { inclusive = true }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val sessionRepository = remember { SessionRepository() }
    val activity = LocalActivity.current as MainActivity

    /**
     * 앱 시작 시 JSESSIONID 쿠키 존재 여부로 시작 화면을 결정합니다.
     *  - 쿠키 있음 → HOME (세션 유지)
     *  - 쿠키 없음 → LOGIN
     */
    val startDestination = if (SessionManager.hasValidSession) Routes.HOME else Routes.LOGIN

    // FCM 알림 탭 딥링크 처리
    LaunchedEffect(activity.pendingFcmIntent) {
        val intent = activity.pendingFcmIntent ?: return@LaunchedEffect
        // 세션 없으면 딥링크 무시 (로그인 화면 유지)
        if (!SessionManager.hasValidSession) {
            activity.consumeFcmIntent()
            return@LaunchedEffect
        }
        val type    = intent.getStringExtra(EXTRA_TYPE)    ?: return@LaunchedEffect
        val notifId = intent.getStringExtra(EXTRA_NOTIF_ID) ?: ""
        activity.consumeFcmIntent()

        when (type) {
            TYPE_SAFETY -> navController.navigate(Routes.safetyDetailRoute(notifId)) {
                popUpTo(Routes.HOME) { saveState = true }
                launchSingleTop = true
            }
            TYPE_DEVICE -> navController.navigate(Routes.DEVICE_DETAIL) {
                popUpTo(Routes.HOME) { saveState = true }
                launchSingleTop = true
            }
        }
    }

    val onLogout: () -> Unit = {
        scope.launch {
            sessionRepository.logout()
            navigateToLogin(navController)
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

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
                onBack                    = { navController.popBackStack() },
                onNavigateToHome          = { navigateTab(navController, Routes.HOME) },
                onNavigateToLive          = { navigateTab(navController, Routes.LIVE) },
                onNavigateToNotifications = { navigateTab(navController, Routes.NOTIFICATIONS) }
            )
        }

        composable(Routes.MY_PAGE) {
            MyPageScreen(
                onBack               = { navController.popBackStack() },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
                onLogout             = onLogout
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
