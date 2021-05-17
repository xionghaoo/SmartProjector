package com.ubtrobot.smartprojector.ui.video

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import com.ubtrobot.smartprojector.GlideApp
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.core.PlainListAdapter

class PlayerItemAdapter(
    items: ArrayList<VideoItem>,
    private val onItemClick: (index: Int) -> Unit
) : PlainListAdapter<VideoItem>(items) {
    override fun itemLayoutId(): Int = R.layout.list_item_player_item

    override fun bindView(v: View, item: VideoItem, position: Int) {
        v.findViewById<TextView>(R.id.tv_video_title).text = item.title
        GlideApp.with(v.context)
            .load(item.url.toUri())
            .into(v.findViewById(R.id.iv_video_icon))

        v.setOnClickListener {
            onItemClick(position)
        }
    }
}