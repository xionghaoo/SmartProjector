package com.ubtrobot.smartprojector.bluetooth

import android.view.View
import android.widget.TextView
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.core.PlainListAdapter

class BluetoothDeviceAdapter(
    private val items: ArrayList<BluetoothDevice>,
    private val itemClick: (position: Int, item: BluetoothDevice) -> Unit
) : PlainListAdapter<BluetoothDevice>(items) {
    override fun itemLayoutId(): Int = R.layout.list_item_bluetooth_device

    override fun bindView(v: View, item: BluetoothDevice, position: Int) {
        v.setOnClickListener {
            itemClick(position, item)
        }
        v.findViewById<TextView>(R.id.tv_bt_device_name).text = "设备名称: ${item.name}\n设备地址：${item.address}"
    }
}