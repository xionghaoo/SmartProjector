package com.ubtrobot.smartprojector.ui.tuya

import android.view.View
import android.widget.Button
import android.widget.TextView
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.sdk.api.IResultCallback
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.core.PlainListAdapter

class TuyaDeviceCmdAdapter(
    items: List<TuyaDeviceCmd>
) : PlainListAdapter<TuyaDeviceCmd>(items) {
    override fun itemLayoutId(): Int = R.layout.list_item_tuya_device_cmd

    override fun bindView(v: View, item: TuyaDeviceCmd, position: Int) {
        v.findViewById<TextView>(R.id.tv_cmd_key).text = item.key
        v.findViewById<TextView>(R.id.tv_cmd_value).text = item.value?.toString()
        v.findViewById<Button>(R.id.btn_send_cmd).setOnClickListener {
//            val device = TuyaHomeSdk.newDeviceInstance(item.deviceId)
//            device.getDp(item.key, object : IResultCallback {
//                override fun onError(code: String?, error: String?) {
//
//                }
//
//                override fun onSuccess() {
//
//                }
//            })
        }
    }
}