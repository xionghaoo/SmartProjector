package com.ubtrobot.smartprojector.ui.tuya

import android.view.View
import android.widget.TextView
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.core.PlainListAdapter

class TuyaDeviceAdapter(
    private val items: List<TuyaDevice>,
    private val onItemClick: (item: TuyaDevice) -> Unit
) : PlainListAdapter<TuyaDevice>(items) {
    override fun itemLayoutId(): Int = R.layout.list_item_tuya_device

    override fun bindView(v: View, item: TuyaDevice, position: Int) {
        v.setOnClickListener {
            onItemClick(item)
        }
        v.findViewById<TextView>(R.id.tv_device_name).text = "PID: ${item.name}${if (item.isZigBeeWifi) "(网关)" else ""}"
        v.findViewById<TextView>(R.id.tv_device_id).text = "ID: ${item.id}"
        v.findViewById<TextView>(R.id.tv_device_status).text = if (item.isOnline) "在线" else "离线"
        v.findViewById<TextView>(R.id.tv_is_zigbee_wifi).text = "Category: ${item.categoryCode}"
    }
}