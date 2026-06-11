package com.example.android.webrtc

import org.json.JSONObject

/**
 * MQTT 시그널링 채널로 주고받는 메시지 타입.
 *
 * ※ ICE candidate의 type 값은 스펙 기준 "ice" 입니다. ("candidate" 아님)
 *
 * 앱 → 보드 (publish):  offer, ice, bye
 * 보드 → 앱 (subscribe): answer, ice, error
 */
sealed class SignalingMessage {
    data class Offer(val sdp: String) : SignalingMessage()
    data class Answer(val sdp: String) : SignalingMessage()
    data class IceCandidate(
        val candidate: String,
        val sdpMid: String,
        val sdpMLineIndex: Int
    ) : SignalingMessage()
    /** 보드가 최대 세션 수 초과 등의 이유로 보내는 에러 */
    data class Error(
        val reason: String,
        val current: Int = 0,
        val limit: Int   = 0
    ) : SignalingMessage()
    object Bye : SignalingMessage()
}

// ─── 직렬화 (앱 → 보드) ───────────────────────────────────────────────────────

fun SignalingMessage.toJson(): JSONObject = when (this) {
    is SignalingMessage.Offer -> JSONObject().apply {
        put("type", "offer")
        put("sdp", sdp)
    }
    is SignalingMessage.Answer -> JSONObject().apply {
        put("type", "answer")
        put("sdp", sdp)
    }
    is SignalingMessage.IceCandidate -> JSONObject().apply {
        put("type", "ice")            // 스펙: "ice"
        put("candidate", candidate)
        put("sdpMid", sdpMid)
        put("sdpMLineIndex", sdpMLineIndex)
    }
    is SignalingMessage.Error -> JSONObject().apply {
        put("type", "error")
        put("reason", reason)
    }
    is SignalingMessage.Bye -> JSONObject().apply {
        put("type", "bye")
    }
}

// ─── 역직렬화 (보드 → 앱) ─────────────────────────────────────────────────────

fun JSONObject.toSignalingMessage(): SignalingMessage? = when (optString("type")) {
    "offer"  -> SignalingMessage.Offer(sdp = getString("sdp"))
    "answer" -> SignalingMessage.Answer(sdp = getString("sdp"))
    "ice"    -> SignalingMessage.IceCandidate(   // 스펙: "ice"
        candidate     = getString("candidate"),
        sdpMid        = optString("sdpMid", ""),
        sdpMLineIndex = optInt("sdpMLineIndex", 0)
    )
    "error"  -> SignalingMessage.Error(
        reason  = optString("reason", "unknown"),
        current = optInt("current", 0),
        limit   = optInt("limit", 0)
    )
    "bye"    -> SignalingMessage.Bye
    else     -> null
}
