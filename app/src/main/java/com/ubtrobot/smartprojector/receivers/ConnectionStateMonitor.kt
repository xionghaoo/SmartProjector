package com.ubtrobot.smartprojector.receivers

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities

import android.net.NetworkRequest
import timber.log.Timber

/**
 * wifi连接状态监听
 */
class ConnectionStateMonitor : ConnectivityManager.NetworkCallback() {
    private var networkRequest: NetworkRequest? = null
    private var onConnectStateChange: ((isConnected: Boolean) -> Unit)? = null
    private var connectivityManager: ConnectivityManager? = null

    init {
        networkRequest = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build()
    }

    fun enable(context: Context) {
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager?.registerNetworkCallback(networkRequest!!, this)
    }

    fun disable() {
        connectivityManager?.unregisterNetworkCallback(this)
    }

    fun setConnectStateListener(call: (Boolean) -> Unit) {
        onConnectStateChange = call
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        onConnectStateChange?.invoke(true)
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        onConnectStateChange?.invoke(false)
    }
}