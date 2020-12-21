package com.ubtrobot.smartprojector

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.ubtrobot.smartprojector.serialport.SerialPortActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_mqtt_test.setOnClickListener {
            startActivity(Intent(this, MqttActivity::class.java))
        }

        btn_serial_port_test.setOnClickListener {
            startActivity(Intent(this, SerialPortActivity::class.java))
        }

        btn_api_test.setOnClickListener {
            viewModel.apiTest().observe(this, Observer {
                if (it.ip != null) tv_api_result.text = "API测试数据: ip = ${it.ip}"
            })
        }
    }
}