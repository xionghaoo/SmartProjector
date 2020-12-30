package com.ubtrobot.smartprojector.ui.video

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ubtrobot.smartprojector.R

class CacheVideoAdapter(
        private val context: Context,
        var items: List<VideoItem>
) : RecyclerView.Adapter<CacheVideoAdapter.ItemViewHolder>() {
    class ItemViewHolder(v: View) : RecyclerView.ViewHolder(v) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val v = layoutInflater.inflate(R.layout.item_cache_video, parent, false)
        return ItemViewHolder(v)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        val v = holder.itemView
        val ivThumbnail = v.findViewById<ImageView>(R.id.iv_thumbnail)
        Glide.with(context)
                .load(item.url)
                .thumbnail(Glide.with(context).load(item.url))
                .centerCrop()
                .into(ivThumbnail)
        v.findViewById<TextView>(R.id.tv_title).text = item.title
    }

    override fun getItemCount(): Int = items.size


}