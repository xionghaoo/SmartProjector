package com.ubtrobot.smartprojector.wifi

import android.view.View
import android.widget.TextView
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.core.PlainListAdapter

class AccessPointAdapter(
    private val items: List<AccessPoint>
) : PlainListAdapter<AccessPoint>(items) {
    override fun itemLayoutId(): Int = R.layout.list_item_wifi_ssid

    override fun bindView(v: View, item: AccessPoint, position: Int) {
        v.findViewById<TextView>(R.id.tv_ssid).text = item.ssid
    }
}