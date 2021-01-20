package com.ubtrobot.smartprojector.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.ui.video.VideoCacheActivity
import kotlinx.android.synthetic.main.activity_video.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.io.File
import java.lang.Exception


class VideoActivity : AppCompatActivity(), Player.EventListener {

    companion object {
        private const val RC_STORAGE_PERMISSION = 1

        private const val EXTRA_URL = "com.ubtrobot.smartprojector.VideoActivity.EXTRA_URL"

        const val TEST_VIDEO = "http://vfx.mtime.cn/Video/2017/03/31/mp4/170331093811717750.mp4"

        fun start(context: Context?, url: String?) {
            val intent = Intent(context, VideoActivity::class.java)
            intent.putExtra(EXTRA_URL, url)
            context?.startActivity(intent)
        }
    }

    private var player: SimpleExoPlayer? = null
    private var btnFullscreen: ImageView? = null
    private var isFullscreen: Boolean = false

    private var autoPlayUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        autoPlayUrl = intent.getStringExtra(EXTRA_URL)

        btn_download.setOnClickListener {
            VideoDownloadService.start(this, TEST_VIDEO.toUri())
        }

        btn_get_downloads.setOnClickListener {
//            VideoDownloadService.loadDownloads(this)
            startActivity(Intent(this, VideoCacheActivity::class.java))
        }

        btn_clear_cache.setOnClickListener {
            VideoDownloadService.removeDownload(this)
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
        val mediaSourceFactory = DefaultMediaSourceFactory(VideoDownloadService.getDataSourceFactory(this))
        player = SimpleExoPlayer.Builder(applicationContext)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()

        val screenOrientation = requestedOrientation
        // 全屏处理
        isFullscreen = false
        btnFullscreen = player_view.findViewById(R.id.exo_fullscreen_icon)
        btnFullscreen?.setOnClickListener {
            if (isFullscreen) {
                // 全屏状态, 退出全屏
                btnFullscreen?.setImageResource(R.drawable.ic_fullscreen_open)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                supportActionBar?.show()
                requestedOrientation = screenOrientation
                val params = player_view.layoutParams
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = resources.getDimension(R.dimen.player_normal_height).toInt()
                player_view.layoutParams = params
                isFullscreen = false
            } else {
                // 正常状态， 开启全屏
                btnFullscreen?.setImageResource(R.drawable.ic_fullscreen_close)
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
                supportActionBar?.hide()
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                val params = player_view.layoutParams
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = RelativeLayout.LayoutParams.MATCH_PARENT
                player_view.layoutParams = params
                isFullscreen = true
            }
        }

        player!!.addListener(this)
        player_view.player = player

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
                    .into(iv_video_1)
//            player?.seekTo(1, 0)
        }
        player!!.prepare()
        player!!.playWhenReady = true

        iv_video_1.setOnClickListener {
            player?.seekTo(0, 0)
            player!!.playWhenReady = true
        }

        iv_video_2.setOnClickListener {
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
                .into(iv_video_2)
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

//    override fun onDownloadChanged(downloadManager: DownloadManager, download: Download, finalException: Exception?) {
//        // 缓存下载变化
//        btn_download.text = "视频缓存 ${download.percentDownloaded}"
//    }
}