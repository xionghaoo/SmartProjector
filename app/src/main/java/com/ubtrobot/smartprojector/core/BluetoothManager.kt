package com.ubtrobot.smartprojector.core

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent

class BluetoothManager(private val context: Activity) {

    companion object {
        private const val REQUEST_ENABLE_BT = 0
    }


    fun inital() {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth

        } else {
            // 判断蓝牙是否启动
            if (!bluetoothAdapter.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                context.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
        }

    }

}