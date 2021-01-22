package com.ubtrobot.smartprojector.ui.cartoonbook

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.ubtrobot.smartprojector.widgets.flippage.PageFlipView

class FlipTestActivity : AppCompatActivity(), GestureDetector.OnGestureListener {
    private lateinit var gestureDetector: GestureDetector
    private lateinit var pageFlipView: PageFlipView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pageFlipView = PageFlipView(this)
        setContentView(pageFlipView)
        gestureDetector = GestureDetector(this, this)
    }

    override fun onResume() {
        super.onResume()
        pageFlipView.onResume()
    }

    override fun onPause() {
        super.onPause()
        pageFlipView.onPause()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            pageFlipView.onFingerUp(event.x, event.y)
        }
        return gestureDetector.onTouchEvent(event)
    }

    override fun onDown(e: MotionEvent): Boolean {
        pageFlipView.onFingerDown(e.x, e.y)
        return true
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        pageFlipView.onFingerMove(e2.x, e2.y)
        return true
    }

    override fun onShowPress(e: MotionEvent?) {

    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent?) {
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return false
    }
}