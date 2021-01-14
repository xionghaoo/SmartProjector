package com.ubtrobot.smartprojector.ui.restrict

import android.view.View
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.core.PlainListAdapter
import com.ubtrobot.smartprojector.repo.table.ThirdApp

class AppWhiteListAdapter(
    private val items: List<ThirdApp>
) : PlainListAdapter<ThirdApp>(items) {
    override fun itemLayoutId(): Int = R.layout.list_item_third_app

    override fun bindView(v: View, item: ThirdApp, position: Int) {
        v.findViewById<ImageView>(R.id.iv_app_icon).setImageDrawable(item.icon)
        v.findViewById<TextView>(R.id.tv_app_title).text = item.name
    }
}