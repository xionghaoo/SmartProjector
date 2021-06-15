package com.ubtrobot.smartprojector.utils

import android.os.Handler
import android.os.Message
import android.view.View

/**
 * 防止重复点击策略
 */
class OnceClickStrategy private constructor() {

    companion object {
        private var lastClickTime = 0L
        private var CLICK_INTERVAL = 1000L

        fun onceClick(v: View, time: Long? = null, clickCallback: (v: View) -> Unit) {
            v.setOnClickListener {
                if (System.currentTimeMillis() - lastClickTime > (time ?: CLICK_INTERVAL)) {
                    lastClickTime = System.currentTimeMillis()
                    clickCallback(v)
                } else {
//                    ToastUtil.showToast(v.context, "点击频率过快")
                }
            }
        }
    }

}