package com.ubtrobot.smartprojector.serialport

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.serialport.SerialPortFinder
import android.util.Log
import android.widget.Toast
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.utils.ToastUtil

class SerialPortActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SerialPortActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_serial_port)

        initial()
    }

    private fun initial() {
        val serialPortFinder = SerialPortFinder()
        val devices = serialPortFinder.allDevicesPath
        if (devices.isEmpty()) {
            ToastUtil.showToast(this, "未找到串口设备")
            return
        }
        Log.d(TAG, "devices: ${devices.size}")
    }
}