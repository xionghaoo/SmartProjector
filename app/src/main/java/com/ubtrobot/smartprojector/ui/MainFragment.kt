package com.ubtrobot.smartprojector.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.core.vo.Status
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
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_mqtt_test.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_mqttFragment)
        }

        btn_serial_port_test.setOnClickListener {
            startActivity(Intent(context, SerialPortActivity::class.java))
        }

        btn_api_test.setOnClickListener {
            tv_title.text = "标题发生改变"
            viewModel.apiTest().observe(viewLifecycleOwner, Observer { r ->
                if (r.status == Status.SUCCESS && r.data != null) {
                    if (r.data.ip != null) tv_api_result.text = "API测试数据: ip = ${r.data.ip}"
                }
            })
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