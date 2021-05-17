package com.ubtrobot.smartprojector.ui.video

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.ubtrobot.smartprojector.R

class VideoCacheActivity : AppCompatActivity() {

    private lateinit var adapter: CacheVideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_cache)

//        rc_cache_list.layoutManager = LinearLayoutManager(this)
//        adapter = CacheVideoAdapter(this, List(20, { VideoItem("测试视频1", VideoActivity.TEST_VIDEO) })) { url ->
//            VideoActivity.start(this, url)
//        }
//        rc_cache_list.adapter = adapter

//        val items = MutableList<VideoItem>(0) {VideoItem("", "")}
//        VideoDownloadHelper.loadDownloads(this)?.values?.forEach { download ->
//            items.add(VideoItem(title = "测试视频", url = download.request.uri.toString()))
//        }

//        adapter.items = items
//        adapter.notifyDataSetChanged()

    }
}