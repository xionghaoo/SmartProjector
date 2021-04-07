package com.ubtrobot.smartprojector.bluetooth

import android.view.View
import android.widget.TextView
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.core.PlainListAdapter

class BluetoothDeviceAdapter(
        private val items: ArrayList<BluetoothDeviceItem>,
        private val itemClick: (position: Int, item: BluetoothDeviceItem) -> Unit
) : PlainListAdapter<BluetoothDeviceItem>(items) {
    override fun itemLayoutId(): Int = R.layout.list_item_bluetooth_device

    override fun bindView(v: View, item: BluetoothDeviceItem, position: Int) {
        v.setOnClickListener {
            itemClick(position, item)
        }
        v.findViewById<TextView>(R.id.tv_bt_device_name).text = "设备名称: ${item.name}\n设备地址：${item.address}"
    }
}