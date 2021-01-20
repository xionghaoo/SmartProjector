package com.ubtrobot.smartprojector.ui.appmarket

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.core.PlainListAdapter
import com.ubtrobot.smartprojector.repo.table.ThirdApp
import com.ubtrobot.smartprojector.utils.ToastUtil
import com.ubtrobot.smartprojector.widgets.AppLauncherView

class AppInfoAdapter(
    private val items: List<ThirdApp>,
    private val itemClick: (pkgName: String) -> Unit
) : PlainListAdapter<ThirdApp>(items) {
    override fun itemLayoutId(): Int = R.layout.item_app_info

    override fun bindView(v: View, item: ThirdApp, position: Int) {
        val itemView = v as AppLauncherView
        itemView.setIcon(item.icon)
        itemView.setLabel(item.name)

        item.isLimited = position % 5 == 0

        if (item.isLimited) {
            itemView.lock {
                ToastUtil.showToast(v.context, "应用已锁定")
            }
        } else {
            itemView.unlock {
                itemClick(item.packageName)
            }

        }

    }
}