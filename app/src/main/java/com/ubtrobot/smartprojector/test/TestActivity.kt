package com.ubtrobot.smartprojector.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.Button
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.WebInterface
import com.ubtrobot.smartprojector.replaceFragment
import eu.chainfire.libsuperuser.Shell
import timber.log.Timber
import java.lang.StringBuilder

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        // 读取Wifi密码
//        Thread {
//            val r = Shell.SU.run("cat /data/misc/wifi/WifiConfigStore.xml")
//            Timber.d("result: ${r?.size}")
//            val strBuilder = StringBuilder()
//            r?.forEach { line ->
//                strBuilder.append(line)
//            }
//            runOnUiThread {
////                test_tv_wifi_info.text = strBuilder.toString()
//            }
//        }.start()

        val webView = findViewById<WebView>(R.id.web_view)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.useWideViewPort = true

        webView.loadUrl("file:///android_asset/js_test.html")
        webView.addJavascriptInterface(WebInterface(this), "Android")

        // 主动调用js方法
        // webView.loadUrl("javascript:javacalljs()")

    }

    private class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            // 重新定义在webview内点击链接时的行为，Android 的默认行为是启动处理网址的应用
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onPageFinished(view: WebView?, url: String?) {

            super.onPageFinished(view, url)
        }
    }

    private class MyWebChromeClient : WebChromeClient() {

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
        }

        override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {

            return super.onJsAlert(view, url, message, result)
        }

    }
}