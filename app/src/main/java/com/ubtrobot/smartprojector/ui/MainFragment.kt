package com.ubtrobot.smartprojector.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.serialport.SerialPortActivity
import com.ubtrobot.smartprojector.utils.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_main.*

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_home.setOnClickListener {
            ToastUtil.showToast(requireContext(), "首页")
        }

        btn_education.setOnClickListener {
            ToastUtil.showToast(requireContext(), "视频教学")
        }

        btn_mqtt_test.setOnClickListener {
//            startActivity(Intent(this, MqttActivity::class.java))
            Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_mqttFragment)
        }

        btn_serial_port_test.setOnClickListener {
            startActivity(Intent(context, SerialPortActivity::class.java))
        }

        btn_api_test.setOnClickListener {
//            viewModel.apiTest().observe(this, Observer {
//                if (it.ip != null) tv_api_result.text = "API测试数据: ip = ${it.ip}"
//            })
            tv_title.text = "标题发生改变"
            viewModel.apiTest()
        }

        btn_video.setOnClickListener {
            startActivity(Intent(context, VideoActivity::class.java))
        }

        btn_tuya.setOnClickListener {
            startActivity(Intent(context, TuyaActivity::class.java))
        }

        btn_file_download.setOnClickListener {
            startActivity(Intent(context, FileDownloadActivity::class.java))
        }
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}