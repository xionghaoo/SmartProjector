package com.ubtrobot.smartprojector.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.replaceFragment
import eu.chainfire.libsuperuser.Shell
import timber.log.Timber
import java.lang.StringBuilder

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        // 读取Wifi密码
        Thread {
            val r = Shell.SU.run("cat /data/misc/wifi/WifiConfigStore.xml")
            Timber.d("result: ${r?.size}")
            val strBuilder = StringBuilder()
            r?.forEach { line ->
                strBuilder.append(line)
            }
            runOnUiThread {
//                test_tv_wifi_info.text = strBuilder.toString()
            }
        }.start()

//        findViewById<View>(R.id.btn_f1).setOnFocusChangeListener { _, hasFocus ->
//            Timber.d("f1 focus: $hasFocus")
//        }
//        findViewById<View>(R.id.btn_f2).setOnFocusChangeListener { _, hasFocus ->
//            Timber.d("f2 focus: $hasFocus")
//        }
    }
}