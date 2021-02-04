package com.ubtrobot.smartprojector.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import kotlinx.android.synthetic.main.fragment_wifi.*

class WifiUtil {
    companion object {
        fun getWifiSSID(context: Context) : String? {
            val appContext = context.applicationContext
            val wifiManager = appContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            // 获取本机WIFI
            val connManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            return if (networkInfo?.isConnected == true) {
                val connInfo = wifiManager.connectionInfo
                connInfo.ssid.substring(1, connInfo.ssid.length - 1)
            } else {
                null
            }
        }
    }
}