package com.example.android.ui.theme

import androidx.compose.ui.unit.dp

/**
 * 앱 전체에서 사용하는 spacing / radius 상수.
 *
 * 컴포넌트마다 흩어진 매직 넘버를 하나의 출처에서 관리하기 위해 추가.
 */
object Dimens {

    // ── Spacing ──────────────────────────────────────────────────────────────
    val spaceXs = 4.dp
    val spaceS = 8.dp
    val spaceM = 12.dp
    val spaceL = 16.dp
    val spaceXl = 20.dp
    val spaceXxl = 24.dp

    // ── Shape radius ─────────────────────────────────────────────────────────
    val radiusS = 16.dp   // 태그, 배지
    val radiusM = 18.dp   // 아이콘 박스 (작은)
    val radiusL = 22.dp   // 아이콘 박스 (큰)
    val radiusCard = 24.dp   // 일반 카드
    val radiusXl = 28.dp   // 상태 카드
    val radiusPage = 32.dp   // 페이지 상단 라운딩, TopAppBar

    // ── Icon / Avatar sizes ───────────────────────────────────────────────────
    val iconS = 18.dp
    val iconM = 24.dp
    val iconL = 28.dp
    val iconXl = 34.dp

    val avatarM = 56.dp   // 이벤트 카드 아이콘 박스
    val avatarL = 72.dp   // 상태 카드 아이콘 박스
}