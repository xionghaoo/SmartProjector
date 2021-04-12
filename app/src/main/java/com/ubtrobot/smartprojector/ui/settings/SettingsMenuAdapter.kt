package com.ubtrobot.smartprojector.ui.settings

import android.view.View
import android.widget.TextView
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.core.PlainListAdapter

class SettingsMenuAdapter(
        private val items: List<String>,
        private val onItemSelected: (position: Int) -> Unit
) : PlainListAdapter<String>(items) {

    private var selectionPosition = 0

    override fun itemLayoutId(): Int = R.layout.list_item_settings_menu

    override fun bindView(v: View, item: String, position: Int) {
        v.findViewById<TextView>(R.id.tv_settings_menu_title).text = item
        if (selectionPosition == position) {
            v.setBackgroundColor(v.context.resources.getColor(R.color.color_menu_item_highlight))
        } else {
            v.setBackgroundColor(v.context.resources.getColor(android.R.color.transparent))
        }
        v.setOnClickListener {
            selectionPosition = position
            onItemSelected(selectionPosition)
            notifyDataSetChanged()
        }
    }
}