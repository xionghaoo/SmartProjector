package com.ubtrobot.smartprojector

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
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

        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo

        wifiInfo.ssid
    }

    private fun getWifiSSID() {
        val connManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun onDestroy() {
        TuyaHomeSdk.onDestroy()
        super.onDestroy()
    }
}