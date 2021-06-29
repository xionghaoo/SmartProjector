package com.ubtrobot.smartprojector.utils

import android.content.Context
import android.content.Intent

class TadpoleMarketManager {
    companion object {
        fun start(context: Context) {
            try {
                val i = Intent()
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .setClassName("xh.zero.tadpolemarket", "xh.zero.tadpolemarket.SplashActivity")
                context.startActivity(i)
            } catch (e: Exception) {
                e.printStackTrace()
                ToastUtil.showToast(context, "页面启动失败")
            }
        }
    }
}