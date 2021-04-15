package com.ubtrobot.smartprojector.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.SupplicantState
import android.net.wifi.WifiManager
import timber.log.Timber

class WifiUtil {
    companion object {

        // 需要位置权限
        fun getWifiSSID(context: Context, call: (String?) -> Unit) {
            val appContext = context.applicationContext
            val wifiManager = appContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            // 获取本机WIFI
            val connManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            return if (networkInfo?.isConnected == true) {
                val connInfo = wifiManager.connectionInfo
                if (connInfo.supplicantState == SupplicantState.COMPLETED) {
                    call(connInfo.ssid.substring(1, connInfo.ssid.length - 1))
                } else {
                    call(null)
                }
            } else {
                call(null)
            }
        }

        fun getWifiLevel(context: Context) : Int {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val numberOfLevels = 5
            val wifiInfo = wifiManager.connectionInfo
            return WifiManager.calculateSignalLevel(wifiInfo.rssi, numberOfLevels)
        }
    }
}