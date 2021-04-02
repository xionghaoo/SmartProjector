package com.ubtrobot.smartprojector.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

class GetLearnReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        Timber.d("action = $action")
        if (action == "com.wyt.ybx.register") {
            val code = intent.getStringExtra("register_result_code")
            // 注册成功 1, 失败 0
            Timber.d("code = $code")

        }
    }
}