package com.ubtrobot.smartprojector.ui

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_video.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.io.File


class VideoActivity : AppCompatActivity() {

    companion object {
        private const val RC_STORAGE_PERMISSION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        videoPlayTask()
    }

    @AfterPermissionGranted(RC_STORAGE_PERMISSION)
    private fun videoPlayTask() {
        if (hasStoragePermission()) {
            Timber.d("has permission")
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

            Glide.with(this)
                .load(path.toUri())
                .centerCrop()
                .into(iv_thumbnail)
        } else {
            EasyPermissions.requestPermissions(
                this,
                "App需要读取外置存储卡，请授予权限",
                RC_STORAGE_PERMISSION,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
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
        return EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
}