package com.ubtrobot.smartprojector

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ubtrobot.smartprojector.serialport.SerialPortActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_mqtt_test.setOnClickListener {
            startActivity(Intent(this, MqttActivity::class.java))
        }

        btn_serial_port_test.setOnClickListener {
            startActivity(Intent(this, SerialPortActivity::class.java))
        }
    }
}