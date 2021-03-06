package com.ubtrobot.smartprojector.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ubtrobot.smartprojector.MqttClient
import com.ubtrobot.smartprojector.R
import dagger.hilt.android.AndroidEntryPoint
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage
import javax.inject.Inject

/**
 * MQTT测试页面
 */
@AndroidEntryPoint
class MqttFragment : Fragment() {

    @Inject
    lateinit var mqttClient: MqttClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mqtt, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        btn_connect.setOnClickListener {
//            mqttClient.connect(object : MqttCallback {
//                override fun connectionLost(cause: Throwable?) {
//                    tv_connect_status.text = "连接断开"
//                    Log.d(MqttClient.TAG, "connectionLost")
//                }
//
//                override fun messageArrived(topic: String?, message: MqttMessage?) {
//                    tv_new_message.text = "$topic:${String(message!!.payload)}"
//                    Log.d(MqttClient.TAG, "messageArrived: ${message.id}, ${message.payload}")
//                }
//
//                override fun deliveryComplete(token: IMqttDeliveryToken?) {
//                    Log.d(MqttClient.TAG, "deliveryComplete: ${token}")
//                }
//            }, success = {
//                tv_connect_status.text = "连接成功"
//            }, failure = { error ->
//                tv_connect_status.text = "连接失败：$error"
//            })
//        }
//
//        edt_subscribe_topic.setText("topic/1")
//        tv_subscribe_status.text = "未订阅"
//        btn_subscribe_topic.setOnClickListener {
//            val topic = edt_subscribe_topic.text.toString()
//            mqttClient.subscribe(topic, success = {
//                tv_subscribe_status.text = "订阅成功"
//                tv_current_topic.text = "当前主题：$topic"
//            }, failure = { error ->
//                tv_subscribe_status.text = "订阅失败"
//            })
//        }
//        btn_send.setOnClickListener {
//            val message = edt_send_message.text.toString()
//            val topic = edt_subscribe_topic.text.toString()
//            mqttClient.publish(topic, message)
//        }
//        btn_disconnect.setOnClickListener {
//            mqttClient.disconnect()
//        }

    }

    companion object {

        fun newInstance() = MqttFragment()
    }
}