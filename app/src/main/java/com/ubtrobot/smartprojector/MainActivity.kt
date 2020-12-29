package com.ubtrobot.smartprojector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.ubtrobot.smartprojector.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onDestroy() {
        TuyaHomeSdk.onDestroy()
        super.onDestroy()
    }
}