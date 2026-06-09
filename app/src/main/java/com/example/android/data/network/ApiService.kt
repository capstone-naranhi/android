package com.example.android.data.network

import com.example.android.data.model.DeviceDetailData
import com.example.android.data.model.HomeData
import com.example.android.data.model.NotificationDetailData
import com.example.android.data.model.NotificationListData
import com.example.android.data.model.UnreadCountData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// ─── 공통 응답 래퍼 ─────────────────────────────────────────────────────────────

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val error: ErrorDto?
)

data class ErrorDto(
    val code: String,
    val message: String
)

// ─── 로그인 응답 모델 ────────────────────────────────────────────────────────────

/** 로그인 성공 시 서버가 반환하는 세션 사용자 정보 */
data class SessionUser(
    val id: Long,
    val name: String,
    val nickname: String,
    val email: String,
    val role: String
)

/** GET /api/v1/auth/me 응답 */
data class MyInfo(
    val id: Long,
    val name: String,
    val email: String
)

// ─── 요청 모델 ──────────────────────────────────────────────────────────────────

data class FcmTokenRequest(
    val fcmToken: String,
    val deviceId: String,
    val platformType: String = "ANDROID"
)

// ─── Retrofit 인터페이스 ────────────────────────────────────────────────────────

interface ApiService {

    /**
     * 로그인: Spring Security formLogin 처리.
     * - Content-Type: application/x-www-form-urlencoded
     * - 파라미터명: email, password (SecurityConfig.usernameParameter("email") 기준)
     * - 성공 시 응답 헤더에 Set-Cookie: JSESSIONID 포함
     */
    @FormUrlEncoded
    @POST("api/v1/auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<ApiResponse<SessionUser>>

    /**
     * 로그아웃: Spring Security 자동 처리.
     * - 서버 세션 무효화 + JSESSIONID 쿠키 삭제
     */
    @POST("api/v1/auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    /**
     * 내 정보 조회.
     * - 세션 쿠키가 없으면 401 반환
     */
    @GET("api/v1/auth/me")
    suspend fun getMyInfo(): Response<ApiResponse<MyInfo>>

    /**
     * 홈 화면 데이터 조회.
     * - 현재 안전 상태, 오늘 요약, 기기 목록, 최근 알림, 기기 상태 반환
     */
    @GET("api/v1/home")
    suspend fun getHome(): Response<ApiResponse<HomeData>>

    /**
     * 알림 상세 조회.
     * - notificationId: 알림 PK (Long)
     * - 응답 type에 따라 safetyDetail / deviceDetail / generalDetail 중 하나만 non-null
     */
    @GET("api/v1/notifications/{notificationId}")
    suspend fun getNotificationDetail(
        @Path("notificationId") notificationId: Long
    ): Response<ApiResponse<NotificationDetailData>>

    /**
     * 알림 읽음 처리.
     */
    @PATCH("api/v1/notifications/{notificationId}/read")
    suspend fun readNotification(
        @Path("notificationId") notificationId: Long
    ): Response<ApiResponse<Unit>>

    /**
     * 알림 목록 조회 (커서 기반 페이지네이션).
     * - type: SAFETY / DEVICE / GENERAL (null 이면 전체)
     * - cursorId: 이전 응답의 nextCursorId (null 이면 첫 페이지)
     */
    @GET("api/v1/notifications")
    suspend fun getNotificationList(
        @Query("type") type: String? = null,
        @Query("cursorId") cursorId: Long? = null
    ): Response<ApiResponse<NotificationListData>>

    /**
     * 미읽음 알림 수 조회.
     */
    @GET("api/v1/notifications/unread-count")
    suspend fun getUnreadCount(): Response<ApiResponse<UnreadCountData>>

    /**
     * 장치 상세 조회.
     * - boardStatus / cameraStatus / micStatus: ONLINE / OFFLINE
     * - heartbeatStatus: 마지막 하트비트로부터 60초 이내면 ONLINE
     * - statusChangeLogs: 최근 20건, changedAt 내림차순
     */
    @GET("api/v1/devices/{deviceId}")
    suspend fun getDeviceDetail(
        @Path("deviceId") deviceId: Long
    ): Response<ApiResponse<DeviceDetailData>>

    /**
     * FCM 토큰 등록·갱신: 앱 실행 시마다 호출하여 항상 최신 토큰을 유지합니다.
     * - platformType은 항상 ANDROID로 고정
     * - deviceId로 기기를 식별해 upsert 처리
     */
    @PUT("api/v1/fcm/token")
    suspend fun registerFcmToken(@Body request: FcmTokenRequest): Response<ApiResponse<Unit>>
}
