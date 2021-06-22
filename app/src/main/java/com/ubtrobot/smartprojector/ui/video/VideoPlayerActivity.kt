package com.ubtrobot.smartprojector.ui.video

import android.Manifest
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.ubtrobot.smartprojector.BuildConfig
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.ActivityVideoPlayerBinding
import com.ubtrobot.smartprojector.utils.ToastUtil
import com.ubtrobot.smartprojector.utils.VideoFullscreenHelper
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class VideoPlayerActivity : AppCompatActivity() {

    companion object {
        private const val RC_STORAGE_PERMISSION = 1

        private const val EXTRA_VIDEO_ITEMS = "${BuildConfig.APPLICATION_ID}.VideoPlayerActivity.EXTRA_VIDEO_ITEMS"

        fun start(context: Context, data: ArrayList<VideoItem>) {
            val intent = Intent(context, VideoPlayerActivity::class.java)
            intent.putParcelableArrayListExtra(EXTRA_VIDEO_ITEMS, data)
            context.startActivity(intent)
        }

    }

    private lateinit var binding: ActivityVideoPlayerBinding
    private var player: SimpleExoPlayer? = null
    private var items: ArrayList<VideoItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        items = intent.getParcelableArrayListExtra(EXTRA_VIDEO_ITEMS)

        binding.rcVideoList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rcVideoList.adapter = PlayerItemAdapter(items ?: ArrayList(), PlayerItemAdapter.Type.GRID) { index ->
            player?.seekTo(index, 0)
            player?.playWhenReady = true
        }

        binding.playerList.translationX = resources.getDimension(R.dimen._300dp)
        binding.btnPlayerList.setOnClickListener {
            binding.playerList.animate()
                .translationX(0f)
                .start()
        }

        binding.btnPlayerListClose.setOnClickListener {
            binding.playerList.animate()
                .translationX(resources.getDimension(R.dimen._300dp))
                .start()
        }

        binding.rcPlayerList.layoutManager = LinearLayoutManager(this)
        binding.rcPlayerList.adapter = PlayerItemAdapter(items ?: ArrayList(), PlayerItemAdapter.Type.LIST) { index ->
            player?.seekTo(index, 0)
            player?.playWhenReady = true
        }

        videoPlayerTask()
    }

    override fun onDestroy() {
        player?.release()
        super.onDestroy()
    }

    @AfterPermissionGranted(RC_STORAGE_PERMISSION)
    fun videoPlayerTask() {
        if (hasStoragePermission()) {
            initialPlayer()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "视频播放需要外置存储卡权限，请授予",
                RC_STORAGE_PERMISSION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun hasStoragePermission() : Boolean {
        return EasyPermissions.hasPermissions(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    private fun initialPlayer() {
        val mediaSourceFactory = DefaultMediaSourceFactory(VideoDownloadHelper.getDataSourceFactory(this))
        player = SimpleExoPlayer.Builder(applicationContext)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()

        // 视频全屏处理
        VideoFullscreenHelper.handle(
            activity = this,
            playView = binding.playerView,
            btnFullscreen = binding.playerView.findViewById(R.id.exo_fullscreen_icon),
            defaultHeight = resources.getDimension(R.dimen._500dp).toInt()
        ) { isFullScreen ->
            if (!isFullScreen) {
                binding.rcVideoList.adapter?.notifyDataSetChanged()
                binding.btnPlayerList.visibility = View.GONE
            } else {
                binding.btnPlayerList.visibility = View.VISIBLE
            }
        }

        binding.playerView.player = player
        player?.apply {
            items?.forEach { item ->
                addMediaItem(MediaItem.fromUri(item.url))
            }

            prepare()
            playWhenReady = true
        }

    }
}