package com.ubtrobot.smartprojector.ui.appmarket

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.core.PlainListAdapter

class AppInfoAdapter(
    private val items: List<AppInfo>,
    private val itemClick: (pkgName: String) -> Unit
) : PlainListAdapter<AppInfo>(items) {
    override fun itemLayoutId(): Int = R.layout.item_app_info

    override fun bindView(v: View, item: AppInfo, position: Int) {
        v.setOnClickListener {
            itemClick(item.packageName)
        }
        v.findViewById<ImageView>(R.id.iv_app_icon).setImageDrawable(item.icon)
        v.findViewById<TextView>(R.id.tv_app_label).text = item.name
    }
}