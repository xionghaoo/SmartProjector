package com.ubtrobot.smartprojector.utils

import android.content.pm.ActivityInfo
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.ubtrobot.smartprojector.R

/**
 * 视频全屏操作
 */
class VideoFullscreenHelper {
    companion object {
        private var isFullscreen: Boolean = false

        fun handle(
                activity: AppCompatActivity,
                playView: ViewGroup,
                btnFullscreen: ImageView?,
                defaultHeight: Int = 200,
                onScreenStateChange: ((isFullScreen: Boolean) -> Unit)? = null
        ) {
            activity.apply {
                val screenOrientation = requestedOrientation
                // 全屏处理
                isFullscreen = false
                btnFullscreen?.setOnClickListener {
                    if (isFullscreen) {
                        // 全屏状态, 退出全屏
                        btnFullscreen.setImageResource(R.drawable.ic_fullscreen_open)
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                        supportActionBar?.show()
                        requestedOrientation = screenOrientation
                        val params = playView.layoutParams
                        params.width = ViewGroup.LayoutParams.MATCH_PARENT
                        params.height = defaultHeight
                        playView.layoutParams = params
                        isFullscreen = false
                    } else {
                        // 正常状态， 开启全屏
                        btnFullscreen.setImageResource(R.drawable.ic_fullscreen_close)
                        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
                        supportActionBar?.hide()
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        val params = playView.layoutParams
                        params.width = ViewGroup.LayoutParams.MATCH_PARENT
                        params.height = RelativeLayout.LayoutParams.MATCH_PARENT
                        playView.layoutParams = params
                        isFullscreen = true
                    }
                    onScreenStateChange?.invoke(isFullscreen)
                }
            }
        }
    }

}