package com.ubtrobot.smartprojector.utils

import android.content.Context
import android.content.Intent

class UcodeAppManager {
    companion object {
        fun start(context: Context) {
            try {
                val i = Intent()
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .setClassName("com.ubtedu.ucode", "com.ubtedu.ucode.launcher.activity")
                context.startActivity(i)
            } catch (e: Exception) {
                e.printStackTrace()
                ToastUtil.showToast(context, "页面启动失败")
            }
        }
    }
}