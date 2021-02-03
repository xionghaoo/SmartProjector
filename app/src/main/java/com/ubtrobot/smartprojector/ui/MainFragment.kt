package com.ubtrobot.smartprojector.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.startPlainActivity
import com.ubtrobot.smartprojector.ui.settings.SettingsActivity
import com.ubtrobot.smartprojector.ui.settings.SettingsFragment
import dagger.hilt.android.AndroidEntryPoint

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

//        btn_mqtt_test.setOnClickListener {
//            Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_mqttFragment)
//        }
//
//        btn_serial_port_test.setOnClickListener {
//            startActivity(Intent(context, SerialPortActivity::class.java))
//        }
//
//        btn_api_test.setOnClickListener {
//            tv_title.text = "标题发生改变"
//            viewModel.apiTest().observe(viewLifecycleOwner, Observer { r ->
//                if (r.status == Status.SUCCESS && r.data != null) {
//                    if (r.data.ip != null) tv_api_result.text = "API测试数据: ip = ${r.data.ip}"
//                }
//            })
//        }
//
//        btn_video.setOnClickListener {
//            startActivity(Intent(context, VideoActivity::class.java))
//        }
//
//        btn_tuya.setOnClickListener {
//            startActivity(Intent(context, TuyaActivity::class.java))
//        }
//
//        btn_file_download.setOnClickListener {
//            startActivity(Intent(context, FileDownloadActivity::class.java))
//        }

        view.findViewById<View>(R.id.card_settings).setOnClickListener {
            startPlainActivity(SettingsActivity::class.java)
        }
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}