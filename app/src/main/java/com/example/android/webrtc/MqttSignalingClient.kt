package com.example.android.webrtc

import android.util.Log
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.json.JSONObject
import java.util.UUID

/**
 * MQTT 브로커를 시그널링 채널로 사용하는 WebRTC 시그널링 클라이언트.
 *
 * 발행 토픽 (앱 → 보드): devices/{deviceSerial}/signaling/client/{sessionId}
 * 구독 토픽 (보드 → 앱): devices/{deviceSerial}/signaling/server/{sessionId}
 */
class MqttSignalingClient(
    private val brokerUrl: String,
    private val deviceSerial: String,
    private val sessionId: String
) {
    companion object {
        private const val TAG = "MqttSignaling"
        private const val QOS_SIGNALING = 1   // 스펙: 시그널링 토픽은 QoS 1
    }

    private val publishTopic   = "devices/$deviceSerial/signaling/client/$sessionId"
    private val subscribeTopic = "devices/$deviceSerial/signaling/server/$sessionId"

    private var mqttClient: MqttAsyncClient? = null

    /** 보드로부터 시그널링 메시지 수신 시 */
    var onMessage: ((SignalingMessage) -> Unit)? = null
    /** MQTT 연결 완료 시 */
    var onConnected: (() -> Unit)? = null
    /** 에러 발생 시 */
    var onError: ((String) -> Unit)? = null

    fun connect() {
        val clientId = "android-${UUID.randomUUID().toString().take(8)}"
        mqttClient = MqttAsyncClient(brokerUrl, clientId, MemoryPersistence())

        mqttClient?.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                Log.w(TAG, "Connection lost", cause)
                onError?.invoke(cause?.message ?: "연결이 끊어졌습니다")
            }

            override fun messageArrived(topic: String, message: MqttMessage) {
                try {
                    val json = JSONObject(String(message.payload))
                    Log.d(TAG, "Received on $topic: $json")
                    val sigMsg = json.toSignalingMessage() ?: return
                    onMessage?.invoke(sigMsg)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to parse signaling message", e)
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {}
        })

        val options = MqttConnectOptions().apply {
            isCleanSession     = true
            connectionTimeout  = 15
            keepAliveInterval  = 30
        }

        mqttClient?.connect(options, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.d(TAG, "Connected to broker: $brokerUrl")
                subscribe()
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.e(TAG, "Failed to connect to broker", exception)
                onError?.invoke(exception?.message ?: "MQTT 연결 실패")
            }
        })
    }

    private fun subscribe() {
        mqttClient?.subscribe(subscribeTopic, QOS_SIGNALING, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.d(TAG, "Subscribed to: $subscribeTopic")
                onConnected?.invoke()
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.e(TAG, "Subscribe failed", exception)
                onError?.invoke(exception?.message ?: "구독 실패")
            }
        })
    }

    fun publish(message: SignalingMessage) {
        val payload = message.toJson().toString()
        Log.d(TAG, "Publishing to $publishTopic: $payload")
        val mqttMessage = MqttMessage(payload.toByteArray()).apply { qos = QOS_SIGNALING }
        try {
            mqttClient?.publish(publishTopic, mqttMessage)
        } catch (e: Exception) {
            Log.e(TAG, "Publish failed", e)
        }
    }

    fun disconnect() {
        try {
            mqttClient?.disconnect()
            Log.d(TAG, "Disconnected from broker")
        } catch (e: Exception) {
            Log.w(TAG, "Disconnect error", e)
        }
    }
}
