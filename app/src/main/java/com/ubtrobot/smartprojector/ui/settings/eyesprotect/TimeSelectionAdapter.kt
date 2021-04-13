package com.ubtrobot.smartprojector.ui.settings.eyesprotect

import android.view.View
import android.widget.TextView
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.core.PlainListAdapter

/**
 * TODO 待确认
 */
class TimeSelectionAdapter(
    private val items: List<String>
) : PlainListAdapter<String>(items) {
    override fun itemLayoutId(): Int = R.layout.list_item_time_selection

    override fun bindView(v: View, item: String, position: Int) {
        (v as TextView).text = item
    }
}