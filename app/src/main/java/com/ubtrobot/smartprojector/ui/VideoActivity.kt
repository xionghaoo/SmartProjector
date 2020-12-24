package com.ubtrobot.smartprojector.ui

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.ubtrobot.smartprojector.R
import kotlinx.android.synthetic.main.activity_video.*
import timber.log.Timber
import java.io.File


class VideoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)


        val player = SimpleExoPlayer.Builder(this)
            .build()
        player_view.player = player


        val testVideo = "https://storage.googleapis.com/wvmedia/clear/hevc/tears/tears.mpd"
        val path = File(Environment.getExternalStorageDirectory(), "${Environment.DIRECTORY_DOWNLOADS}/video1.mp4")
        Timber.d("download path: ${path}")
        val item = MediaItem.fromUri(path.toUri())
        player.setMediaItem(item)
        player.prepare()
        player.play()
    }
}