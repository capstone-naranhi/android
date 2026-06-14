package com.example.android.fcm

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class FcmEvent(
    val title: String,
    val body: String,
    /** "SAFETY" | "DEVICE" | 기타 */
    val type: String,
    val timeText: String
)

/**
 * 프로세스 내 FCM 이벤트 버스.
 * IbomMessagingService → LiveViewModel 단방향 전달.
 */
object FcmEventBus {
    private val _events = MutableSharedFlow<FcmEvent>(extraBufferCapacity = 16)
    val events: SharedFlow<FcmEvent> = _events.asSharedFlow()

    fun emit(event: FcmEvent) {
        _events.tryEmit(event)
    }
}
