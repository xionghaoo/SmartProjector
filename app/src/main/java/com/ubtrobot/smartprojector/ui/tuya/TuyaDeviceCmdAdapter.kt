package com.ubtrobot.smartprojector.ui.tuya

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.sdk.api.IResultCallback
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.core.PlainListAdapter
import com.ubtrobot.smartprojector.utils.ToastUtil
import timber.log.Timber

class TuyaDeviceCmdAdapter(
    private val context: Context,
    items: List<TuyaDeviceCmd>
) : PlainListAdapter<TuyaDeviceCmd>(items) {
    override fun itemLayoutId(): Int = R.layout.list_item_tuya_device_cmd

    override fun bindView(v: View, item: TuyaDeviceCmd, position: Int) {
        v.findViewById<TextView>(R.id.tv_cmd_key).text = "dpID: ${item.key}"
        val edtValue = v.findViewById<EditText>(R.id.edt_cmd_value)
        edtValue.setText(item.value?.toString())
        v.findViewById<Button>(R.id.btn_send_cmd).setOnClickListener {
            val device = TuyaHomeSdk.newDeviceInstance(item.deviceId)
            val cmd = "{\"${item.key}\": ${edtValue.text}}"
            Timber.d("发送指令：$cmd")
            device.publishDps(cmd, object : IResultCallback {
                override fun onError(code: String?, error: String?) {
                    Timber.d("指令发送失败：$code, $error")
                }

                override fun onSuccess() {
                    ToastUtil.showToast(context, "指令发送成功")
                }
            })
        }
    }
}