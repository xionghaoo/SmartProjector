package com.ubtrobot.smartprojector.bluetooth

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import java.io.IOException
import java.util.*

class BluetoothDelegate(private val activity: Activity,
                        private val pairedDeviceAdd: (name: String?, address: String?) -> Unit,
                        private val newDeviceAdd: (name: String?, address: String?) -> Unit,
                        private val discoveryFinished: () -> Unit) {

    companion object {
        private const val TAG = "BluetoothDelegate"
        private val MY_UUID = UUID.randomUUID()
        private const val REQUEST_ENABLE_BT = 101
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
//            Log.d(TAG, "discovery callback")
            val action: String? = intent?.action
            when (action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // 找到一台设备
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    val deviceHardwareAddress = device?.address
                    if (deviceName != null) {
                        newDeviceAdd(deviceName, deviceHardwareAddress)
                    }
//                    Log.d(TAG, "找到一台设备：${deviceName}, $deviceHardwareAddress")
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    discoveryFinished()
                }
            }
        }
    }

    private var bluetoothAdapter: BluetoothAdapter? = null

    fun onCreate() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter?.isEnabled!!) {
                // 设备没有打开蓝牙，请求用户启动蓝牙
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            } else {
//                Log.d(TAG, "设备支持蓝牙")
                // 已经开启蓝牙
//                queryPairedDevices()
            }
        } else {
            Log.e(TAG, "设备不支持蓝牙")
        }


        var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        activity.registerReceiver(receiver, filter)
        filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        activity.registerReceiver(receiver, filter)
    }

    fun onDestroy() {
        activity.unregisterReceiver(receiver)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_ENABLE_BT) {
            when(resultCode) {
                Activity.RESULT_OK -> {
                    // 成功开启蓝牙
//                    queryPairedDevices()
                }
                Activity.RESULT_CANCELED -> {
                    // 开启蓝牙失败
                }
            }
        }
    }

    fun queryPairedDevices() {
        // 查询已经配对的蓝牙设备
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address
            if (deviceName != null) {
                pairedDeviceAdd(deviceName, deviceHardwareAddress)
            }
        }

        startDeviceDiscovery()
    }

    /**
     * 蓝牙设备发现，需要fine location权限
     */
    fun startDeviceDiscovery() {
        // 开始发现设备
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter?.cancelDiscovery()
        }
        bluetoothAdapter?.startDiscovery()
    }

    fun manageMyConnectedSocket(socket: BluetoothSocket) {

    }

    private inner class AcceptThread : Thread() {
        private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord("bluetooth_accept_socket", MY_UUID)
        }

        override fun run() {
            var shouldLoop = true
            while (shouldLoop) {
                val socket: BluetoothSocket? = try {
                    mmServerSocket?.accept()
                } catch (e: IOException) {
                    Log.e(TAG, "Socket's accept() method failed", e)
                    shouldLoop = false
                    null
                }
                // 获取到一个客户端连接后立即关闭监听
                socket?.also {
                    manageMyConnectedSocket(it)
                    mmServerSocket?.close()
                    shouldLoop = false
                }
            }
        }

        fun cancel() {
            try {
                mmServerSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }
    }

//    private inner class ConnectThread(device: BluetoothDevice) : Thread() {
//        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
//            device.createInsecureRfcommSocketToServiceRecord(MY_UUID)
//        }
//
//        override fun run() {
//            bluetoothAdapter?.cancelDiscovery()
//
//            mmSocket?.use { socket ->
//                socket.connect()
//                manageMyConnectedSocket(socket)
//            }
//        }
//
//        fun cancel() {
//            try {
//                mmSocket?.close()
//            } catch (e: IOException) {
//                Log.e(TAG, "Could not close the client socket", e)
//            }
//        }
//    }
}
