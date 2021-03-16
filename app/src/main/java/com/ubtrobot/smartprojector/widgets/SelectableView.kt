package com.ubtrobot.smartprojector.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import timber.log.Timber

class SelectableView : FrameLayout {

    private var listener: (() -> Unit)? = null
    private var selectedState = 0

    constructor(context: Context) : super(context) {
        initial(context)
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initial(context)
    }

    private fun initial(context: Context) {
        Timber.d("initial: $id")
        isFocusable = true
        isFocusableInTouchMode = true
//        setOnFocusChangeListener { _, hasFocus ->
//            Timber.d("has focus: $id, $hasFocus")
//            if (hasFocus) {
//                animate().scaleX(1.4f)
//                        .scaleY(1.4f)
//                        .translationZ(10f)
//                        .start()
//            } else {
//                animate().scaleX(1f)
//                        .scaleY(1f)
//                        .translationZ(0f)
//                        .start()
//            }
//        }

    }

//    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
//        Timber.d("onInterceptTouchEvent: ${ev?.action}")
//        if (ev != null) {
//            if (ev.actionMasked == MotionEvent.ACTION_DOWN) {
//                animate().cancel()
//                animate().scaleX(1.05f)
//                        .scaleY(1.05f)
////                        .translationZ(5f)
//                        .start()
//            }
//            if (ev.actionMasked == MotionEvent.ACTION_UP || ev.actionMasked == MotionEvent.ACTION_CANCEL) {
//                animate().cancel()
//                animate().scaleX(1f)
//                        .scaleY(1f)
////                        .translationZ(0f)
//                        .start()
//            }
//        }
//        return super.onInterceptTouchEvent(ev)
//    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                animate().cancel()
                animate().scaleX(1.05f)
                        .scaleY(1.05f)
//                        .translationZ(5f)
                        .start()
            }
            if (event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_CANCEL) {
                animate().cancel()
                animate().scaleX(1f)
                        .scaleY(1f)
//                        .translationZ(0f)
                        .start()
                listener?.invoke()
            }
        }
        return true
    }

    fun toggleSelectedState() {
        if (selectedState == 0) {
            selectedState = 1
            animate().scaleX(1.4f)
                    .scaleY(1.4f)
                    .translationZ(10f)
                    .start()
        } else if (selectedState == 1) {
            selectedState = 0
            animate().scaleX(1f)
                    .scaleY(1f)
                    .translationZ(0f)
                    .start()
            listener?.invoke()
        }
    }

    fun setSelectListener(listener: () -> Unit) {
        this.listener = listener
    }
}