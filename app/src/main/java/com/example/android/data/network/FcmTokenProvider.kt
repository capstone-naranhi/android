package com.example.android.data.network

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

/**
 * FCM 토큰을 가져오는 공급자.
 * google-services.json이 app/ 디렉토리에 있어야 정상 동작합니다.
 */
object FcmTokenProvider {

    /**
     * FCM 디바이스 토큰을 반환합니다.
     * 실패 시 null을 반환하며, 토큰 등록 단계는 건너뜁니다.
     */
    suspend fun getToken(): String? = runCatching {
        FirebaseMessaging.getInstance().token.await()
    }.getOrNull()
}
