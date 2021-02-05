package com.ubtrobot.smartprojector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.ubtrobot.smartprojector.databinding.ActivityMqttBinding
import dagger.hilt.android.AndroidEntryPoint
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage
import javax.inject.Inject

@AndroidEntryPoint
class MqttActivity : AppCompatActivity() {

    @Inject
    lateinit var mqttClient: MqttClient
    private lateinit var binding: ActivityMqttBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMqttBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnConnect.setOnClickListener {
            mqttClient.connect(object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    binding.tvConnectStatus.text = "连接断开"
                    Log.d(MqttClient.TAG, "connectionLost")
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    binding.tvNewMessage.text = "$topic:${String(message!!.payload)}"
                    Log.d(MqttClient.TAG, "messageArrived: ${message.id}, ${message.payload}")
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    Log.d(MqttClient.TAG, "deliveryComplete: ${token}")
                }
            }, success = {
                binding.tvConnectStatus.text = "连接成功"
            }, failure = { error ->
                binding.tvConnectStatus.text = "连接失败：$error"
            })
        }

        binding.edtSubscribeTopic.setText("topic/1")
        binding.tvSubscribeStatus.text = "未订阅"
        binding.btnSubscribeTopic.setOnClickListener {
            val topic = binding.edtSubscribeTopic.text.toString()
            mqttClient.subscribe(topic, success = {
                binding.tvSubscribeStatus.text = "订阅成功"
                binding.tvCurrentTopic.text = "当前主题：$topic"
            }, failure = { error ->
                binding.tvSubscribeStatus.text = "订阅失败"
            })
        }
        binding.btnSend.setOnClickListener {
            val message = binding.edtSendMessage.text.toString()
            val topic = binding.edtSubscribeTopic.text.toString()
            mqttClient.publish(topic, message)
        }
        binding.btnDisconnect.setOnClickListener {
            mqttClient.disconnect()
        }

    }
}