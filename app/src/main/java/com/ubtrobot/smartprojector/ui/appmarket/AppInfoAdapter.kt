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
        // com.android.calendar
        // com.android.calculator2
        // com.android.camera2
        // com.android.contacts
        // com.android.music
        // com.android.dialer
        // com.android.settings
        // com.android.deskclock
        // com.android.documentsui
        // app图标替换
        when (item.packageName) {
            "com.android.calendar" -> itemView.setIcon(v.context.getDrawable(R.mipmap.ic_app_calendar))
            "com.android.music" -> itemView.setIcon(v.context.getDrawable(R.mipmap.ic_app_music))
            "com.android.deskclock" -> itemView.setIcon(v.context.getDrawable(R.mipmap.ic_app_clock))
            "com.android.documentsui" -> itemView.setIcon(v.context.getDrawable(R.mipmap.ic_app_file_manager))
        }
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