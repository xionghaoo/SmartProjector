package com.ubtrobot.smartprojector.ui.appmarket

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.core.PlainListAdapter
import com.ubtrobot.smartprojector.repo.table.ThirdApp

class AppInfoAdapter(
    private val items: List<ThirdApp>,
    private val itemClick: (pkgName: String) -> Unit
) : PlainListAdapter<ThirdApp>(items) {
    override fun itemLayoutId(): Int = R.layout.item_app_info

    override fun bindView(v: View, item: ThirdApp, position: Int) {
        v.setOnClickListener {
            itemClick(item.packageName)
        }
        v.findViewById<ImageView>(R.id.iv_app_icon).setImageDrawable(item.icon)
        v.findViewById<TextView>(R.id.tv_app_label).text = item.name
    }
}