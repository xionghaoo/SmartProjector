package com.ubtrobot.smartprojector.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.google.android.exoplayer2.ui.PlayerView

/**
 * 悬浮播放视图
 */
class DragPlayerView : PlayerView {

    // 手指按下屏幕时的坐标值
    private var downX: Float = 0f
    private var downY: Float = 0f
    // 手指离开屏幕时的坐标值
    private var upX: Float = 0f
    private var upY: Float = 0f

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                upX = x + (event.x - downX)
                upY = y + (event.y - downY)
                translationX = upX
                translationY = upY
            }
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> {
                val parentWidth = (parent as View).width
                val parentHeight = (parent as View).height
                // x方向溢出处理
                if (upX + width > parentWidth) {
                    translationX = parentWidth.toFloat() - width
                } else if (upX < 0) {
                    translationX = 0f
                }
                // y方向溢出处理
                if (upY + height > parentHeight) {
                    translationY = parentHeight.toFloat() - height
                } else if (translationY < 0) {
                    translationY = 0f
                }
            }
        }
        return super.onTouchEvent(event)
    }
}