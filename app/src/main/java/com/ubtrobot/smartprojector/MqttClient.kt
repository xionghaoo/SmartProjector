package com.ubtrobot.smartprojector

import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

typealias SuccessCallback = () -> Unit
typealias FailureCallback = (String?) -> Unit

class MqttClient(
    private val context: Context,
    private val serverURI: String,
    /**
     * client id 对客户端来说必须唯一，不然会被挤掉线
     */
    private val clientId: String
) {

    companion object {
        const val TAG = "AndroidMqttClient"
    }

    private var mqttClient = MqttAndroidClient(context, serverURI, clientId)

    fun connect(
        messageCallback: MqttCallback,
        success: SuccessCallback? = null,
        failure: FailureCallback? = null
    ) {

        mqttClient.setCallback(messageCallback)
        val options = MqttConnectOptions()
        try {
            mqttClient.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    success?.invoke()
                    Log.d(TAG, "Connection success")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    failure?.invoke(exception?.localizedMessage)
                    Log.d(TAG, "Connection failure: ${exception?.localizedMessage}")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }

    }

    /**
     * 订阅到主题，只有订阅到主题的客户端才能收到订阅消息
     */
    fun subscribe(topic: String, qos: Int = 1, success: SuccessCallback? = null, failure: FailureCallback? = null) {
        try {
            mqttClient.subscribe(topic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    success?.invoke()
                    Log.d(TAG, "Subscribed to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    failure?.invoke(exception?.localizedMessage)
                    Log.d(TAG, "Failed to subscribe $topic")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun unsubscribe(topic: String) {
        try {
            mqttClient.unsubscribe(topic, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Unsubscribed to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to unsubscribe $topic")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    /**
     * 发布消息，根据主题来发布
     */
    fun publish(topic: String, msg: String, qos: Int = 1, retained: Boolean = false) {
        try {
            val message = MqttMessage()
            message.payload = msg.toByteArray()
            message.qos = qos
            message.isRetained = retained
            mqttClient.publish(topic, message, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "$msg published to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to publish $msg to $topic")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        try {
            mqttClient.disconnect(null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Disconnected")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to disconnect")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }
}