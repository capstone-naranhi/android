package com.example.android.data.network

import com.example.android.data.model.NotificationDetailData

class NotificationRepository {

    private val api get() = RetrofitClient.apiService

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
