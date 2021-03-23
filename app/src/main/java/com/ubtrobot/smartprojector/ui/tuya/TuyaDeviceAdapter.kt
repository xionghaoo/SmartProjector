package com.ubtrobot.smartprojector.ui.tuya

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.ubtrobot.smartprojector.GlideApp
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.core.PlainListAdapter

class TuyaDeviceAdapter(
    private val items: List<TuyaDevice>,
    private val onItemClick: (index: Int, item: TuyaDevice) -> Unit
) : PlainListAdapter<TuyaDevice>(items) {

    private var selectedPosition = 0

    override fun itemLayoutId(): Int = R.layout.list_item_tuya_device_connected

    override fun bindView(v: View, item: TuyaDevice, position: Int) {
        v.setOnClickListener {
            onItemClick(position, item)
            selectedPosition = position
            notifyDataSetChanged()
        }

        v.setBackgroundResource(
                if (selectedPosition == position) {
                    R.drawable.shape_card_selected
                } else {
                    R.drawable.shape_card_normal
                }
        )
//        v.findViewById<TextView>(R.id.tv_device_name).text = "PID: ${item.name}${if (item.isZigBeeWifi) "(网关)" else ""}"
//        v.findViewById<TextView>(R.id.tv_device_id).text = "ID: ${item.id}"
//        v.findViewById<TextView>(R.id.tv_device_status).text = if (item.isOnline) "在线" else "离线"
//        v.findViewById<TextView>(R.id.tv_is_zigbee_wifi).text = "Category: ${item.categoryCode}"

        v.findViewById<TextView>(R.id.tv_tuya_device_name).text = item.name
        GlideApp.with(v.context)
                .load(item.iconUrl)
                .into(v.findViewById<ImageView>(R.id.iv_tuya_device_icon))

        v.findViewById<TextView>(R.id.tv_tuya_device_status).text = if (item.isOnline) "在线" else "离线"
        v.findViewById<TextView>(R.id.v_tuya_device_status_indicator).setBackgroundResource(
                if (item.isOnline) R.drawable.shape_indicator_online else R.drawable.shape_indicator_offline
        )
    }
}