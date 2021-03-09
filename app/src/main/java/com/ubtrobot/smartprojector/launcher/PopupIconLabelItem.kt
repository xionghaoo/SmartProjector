package com.ubtrobot.smartprojector.launcher

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.items.AbstractItem
import com.ubtrobot.smartprojector.R

class PopupIconLabelItem(
    private val _labelRes: Int,
    private val _iconRes: Int
) : AbstractItem<PopupIconLabelItem, PopupIconLabelItem.ViewHolder>() {
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val iconView: ImageView? = v.findViewById<ImageView>(R.id.item_popup_icon)
        val labelView: TextView? = v.findViewById<TextView>(R.id.item_popup_label)
    }

    override fun getType(): Int = R.id.id_adapter_icon_label_item

    override fun getLayoutRes(): Int = R.layout.item_popup_icon_label

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)
        holder.labelView?.setText(_labelRes)
        holder.iconView?.setImageResource(_iconRes)
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.labelView?.text = null
        holder.iconView?.setImageDrawable(null)
    }
}