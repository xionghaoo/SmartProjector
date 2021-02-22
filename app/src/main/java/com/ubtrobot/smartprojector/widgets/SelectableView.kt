package com.ubtrobot.smartprojector.widgets

import android.content.Context
import android.util.AttributeSet
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
        setOnFocusChangeListener { _, hasFocus ->
            Timber.d("has focus: $id, $hasFocus")
            if (hasFocus) {
                animate().scaleX(1.4f)
                        .scaleY(1.4f)
                        .translationZ(10f)
                        .start()
            } else {
                animate().scaleX(1f)
                        .scaleY(1f)
                        .translationZ(0f)
                        .start()
            }
        }

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
        setOnClickListener {
            if (hasFocus()) {
                listener()
            }
        }
    }
}