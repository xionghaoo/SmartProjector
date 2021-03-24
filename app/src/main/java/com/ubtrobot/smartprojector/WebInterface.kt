package com.ubtrobot.smartprojector

import android.content.Context
import android.webkit.JavascriptInterface
import com.tuya.smart.utils.ToastUtil

/**
 * WebView js接口调用
 */
class WebInterface(private val context: Context) {
    @JavascriptInterface
    fun showToast(msg: String) {
        ToastUtil.shortToast(context, msg)
    }
}