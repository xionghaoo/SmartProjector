package com.ubtrobot.smartprojector.ui.appmarket

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.core.PlainListAdapter
import com.ubtrobot.smartprojector.launcher.App
import com.ubtrobot.smartprojector.repo.table.ThirdApp
import com.ubtrobot.smartprojector.ui.MainActivity
import com.ubtrobot.smartprojector.utils.ToastUtil
import com.ubtrobot.smartprojector.widgets.AppLauncherView

class AppInfoAdapter(
    private val items: List<App>,
    private val itemClick: (pkgName: String) -> Unit
) : PlainListAdapter<App>(items) {
    override fun itemLayoutId(): Int = R.layout.item_app_info

    override fun bindView(v: View, item: App, position: Int) {
        val itemView = v.findViewById<AppLauncherView>(R.id.v_app_launcher)
        itemView.setIcon(item.icon)
        itemView.setLabel(item.label)

//        item._isLimited = position % 5 == 0

        if (item._isLimited) {
            itemView.lock {
                ToastUtil.showToast(v.context, "应用已锁定")
            }
        } else {
            itemView.unlock {
                itemClick(item.packageName)
            }
            if (MainActivity.getLauncher() != null) {
                itemView.setOnLongClickListener(MainActivity.getLauncher()!!.getItemOptionView().getLongClickListener(item))
            }
        }
    }
}