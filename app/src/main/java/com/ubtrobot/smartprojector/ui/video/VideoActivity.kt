package com.ubtrobot.smartprojector.ui.video

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.ubtrobot.smartprojector.MockData
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.ActivityVideoBinding
import com.ubtrobot.smartprojector.utils.VideoFullscreenHelper
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File


class VideoActivity : AppCompatActivity(), Player.EventListener {

    companion object {
        private const val RC_STORAGE_PERMISSION = 1

        private const val EXTRA_URL = "com.ubtrobot.smartprojector.VideoActivity.EXTRA_URL"

        const val TEST_VIDEO = MockData.video3

        fun start(context: Context?, url: String?) {
            val intent = Intent(context, VideoActivity::class.java)
            intent.putExtra(EXTRA_URL, url)
            context?.startActivity(intent)
        }
    }

    private var player: SimpleExoPlayer? = null

    private var autoPlayUrl: String? = null
    private lateinit var binding: ActivityVideoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        autoPlayUrl = intent.getStringExtra(EXTRA_URL)

        binding.btnDownload.setOnClickListener {
            VideoCacheDownloadService.start(this, TEST_VIDEO)
        }

        binding.btnGetDownloads.setOnClickListener {
            startActivity(Intent(this, VideoCacheActivity::class.java))
        }

        // 清除视频缓存
        binding.btnClearCache.setOnClickListener {
            VideoCacheDownloadService.removeDownload(this, TEST_VIDEO)
        }

        videoPlayTask()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        autoPlayUrl = intent?.getStringExtra(EXTRA_URL)

        if (autoPlayUrl != null) {
            player?.seekTo(1, 0)
        }
        player!!.playWhenReady = true
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
//        player_view.onPause()
    }

    override fun onResume() {
        super.onResume()
//        player_view.onResume()
    }

    override fun onStop() {
        super.onStop()

    }

    override fun onDestroy() {
        player?.release()
        super.onDestroy()
    }

    @AfterPermissionGranted(RC_STORAGE_PERMISSION)
    private fun videoPlayTask() {
        if (hasStoragePermission()) {
            initialPlayer()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "App需要读取外置存储卡，请授予权限",
                RC_STORAGE_PERMISSION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
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
                defaultHeight = resources.getDimension(R.dimen.player_normal_height).toInt()
        )

        player!!.addListener(this)
        binding.playerView.player = player
        binding.playerView.findViewById<TextView>(R.id.exo_video_title).text = "测试视频"

        val testVideo = "https://storage.googleapis.com/wvmedia/clear/hevc/tears/tears.mpd"
//        val video1 = File(
//            Environment.getExternalStorageDirectory(),
//            "${Environment.DIRECTORY_DOWNLOADS}/The Angry Birds Movie.mp4"
//        )
        val video2 = File(
            Environment.getExternalStorageDirectory(),
            "${Environment.DIRECTORY_DOWNLOADS}/video1.mp4"
        )
//        Timber.d("download path: ${video1}")
//        val item1 = MediaItem.fromUri(video1.toUri())
//        val item2 = MediaItem.fromUri(video2.toUri())
//        player!!.addMediaItem(item1)
//        player!!.addMediaItem(item2)
//        player!!.prepare()

        if (autoPlayUrl != null) {
            player!!.addMediaItem(MediaItem.fromUri(autoPlayUrl!!.toUri()))
            Glide.with(this)
                    .load(autoPlayUrl!!.toUri())
                    .centerCrop()
                    .into(binding.ivVideo1)
//            player?.seekTo(1, 0)
        }
        player!!.addMediaItem(MediaItem.fromUri(TEST_VIDEO.toUri()))
        player!!.prepare()
        player!!.playWhenReady = true

        binding.ivVideo1.setOnClickListener {
            player?.seekTo(0, 0)
            player!!.playWhenReady = true
        }

        binding.ivVideo2.setOnClickListener {
            player?.seekTo(1, 0)
            player?.playWhenReady = true
        }

        // 加载视频缩略图
//        Glide.with(this)
//                .load(video1.toUri())
//                .centerCrop()
//                .into(iv_video_1)

        Glide.with(this)
                .load(TEST_VIDEO)
                .thumbnail(Glide.with(this).load(TEST_VIDEO))
                .centerCrop()
                .into(binding.ivVideo2)
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

//    // permission callback
//    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
//        Timber.d("onPermissionsGranted: ${requestCode}, ${perms}")
//    }
//
//    // 显示授权窗口deny时调用，显示Rationale窗口deny时也会调用
//    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
//        Timber.d( "onPermissionsDenied: ${requestCode}, ${perms}")
//    }
//
//    // rationale callback
//    override fun onRationaleAccepted(requestCode: Int) {
//        Timber.d( "onRationaleAccepted: ${requestCode}")
//
//    }
//
//    // 显示Rationale窗口deny时调用
//    override fun onRationaleDenied(requestCode: Int) {
//        Timber.d( "onRationaleDenied: ${requestCode}")
//    }

    override fun onPlaybackStateChanged(state: Int) {

    }
}