package com.ubtrobot.smartprojector.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import com.alibaba.sdk.android.push.CloudPushService
import com.alibaba.sdk.android.push.CommonCallback
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory


class AliPushManager {
    companion object {

        private const val TAG = "AliPushManager"

        /**
         * 初始化云推送通道
         * @param context
         */
        fun initCloudChannel(context: Context) {
            PushServiceFactory.init(context)
            val pushService: CloudPushService = PushServiceFactory.getCloudPushService()
            pushService.register(context, object : CommonCallback {
                override fun onSuccess(response: String?) {
                    Log.d(TAG, "init cloudchannel success")
                }

                override fun onFailed(errorCode: String, errorMessage: String) {
                    Log.d(TAG, "init cloudchannel failed -- errorcode:$errorCode -- errorMessage:$errorMessage")
                }
            })

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
                // 通知渠道的id。
                val id = "1"
                // 用户可以看到的通知渠道的名字。
                val name: CharSequence = "aliyun"
                // 用户可以看到的通知渠道的描述。
                val description = "aliyun push"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val mChannel = NotificationChannel(id, name, importance)
                // 配置通知渠道的属性。
                mChannel.description = description
                // 设置通知出现时的闪灯（如果Android设备支持的话）。
                mChannel.enableLights(true)
                mChannel.lightColor = Color.RED
                // 设置通知出现时的震动（如果Android设备支持的话）。
                mChannel.enableVibration(true)
                mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                // 最后在notificationmanager中创建该通知渠道。
                mNotificationManager!!.createNotificationChannel(mChannel)
            }
        }
    }
}