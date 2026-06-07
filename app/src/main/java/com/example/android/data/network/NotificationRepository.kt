package com.example.android.data.network

import com.example.android.data.model.NotificationDetailData
import com.example.android.data.model.NotificationListData

class NotificationRepository {

    private val api get() = RetrofitClient.apiService

    /** 알림 목록 조회. type null = 전체, cursorId null = 첫 페이지 */
    suspend fun getNotificationList(
        type: String? = null,
        cursorId: Long? = null
    ): Result<NotificationListData> = runCatching {
        val response = api.getNotificationList(type, cursorId)
        if (!response.isSuccessful) {
            error("알림 목록 로드 실패 (${response.code()})")
        }
        val body = response.body()
        if (body?.success != true || body.data == null) {
            error(body?.error?.message ?: "알림 목록을 불러올 수 없습니다.")
        }
        body.data
    }

    /** 미읽음 알림 수 조회 */
    suspend fun getUnreadCount(): Result<Int> = runCatching {
        val response = api.getUnreadCount()
        if (!response.isSuccessful) error("미읽음 수 로드 실패 (${response.code()})")
        val body = response.body()
        if (body?.success != true || body.data == null) error("미읽음 수를 불러올 수 없습니다.")
        body.data.unreadCount
    }

    /** 알림 상세 조회. notificationId는 String → Long 변환 후 호출 */
    suspend fun getNotificationDetail(notificationId: String): Result<NotificationDetailData> = runCatching {
        val id = notificationId.toLongOrNull()
            ?: error("유효하지 않은 알림 ID입니다.")
        val response = api.getNotificationDetail(id)
        if (!response.isSuccessful) {
            val msg = when (response.code()) {
                401  -> "로그인이 필요합니다."
                403  -> "접근 권한이 없습니다."
                404  -> "알림을 찾을 수 없습니다."
                else -> "알림 로드 실패 (${response.code()})"
            }
            error(msg)
        }
        val body = response.body()
        if (body?.success != true || body.data == null) {
            error(body?.error?.message ?: "알림을 불러올 수 없습니다.")
        }
        body.data
    }

    /** 알림 읽음 처리. 실패해도 무시 */
    suspend fun readNotification(notificationId: String) {
        val id = notificationId.toLongOrNull() ?: return
        runCatching { api.readNotification(id) }
    }
}
