package com.ubtrobot.smartprojector.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.compose.ui.platform.ViewAmbient
import com.ubtrobot.smartprojector.R
import kotlinx.android.synthetic.main.widget_app_launcher.view.*

class AppLauncherView : FrameLayout {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        inflate(context, R.layout.widget_app_launcher, this)
    }

    fun lock(callback: () -> Unit) {
        iv_app_lock.visibility = View.VISIBLE
        setOnClickListener(null)
        iv_app_lock.setOnClickListener { callback() }
    }

    fun unlock(callback: () -> Unit) {
        iv_app_lock.visibility = View.GONE
        setOnClickListener { callback() }
    }

    fun setIcon(drawable: Drawable?) {
        iv_app_icon.setImageDrawable(drawable)
    }

    fun setLabel(label: String?) {
        tv_app_label.text = label
    }
}