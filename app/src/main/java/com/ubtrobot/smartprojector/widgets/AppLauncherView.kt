package com.ubtrobot.smartprojector.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.ubtrobot.smartprojector.databinding.WidgetAppLauncherBinding

class AppLauncherView : FrameLayout {

    private lateinit var binding: WidgetAppLauncherBinding

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        binding = WidgetAppLauncherBinding.inflate(LayoutInflater.from(context), this, false)
    }

    fun lock(callback: () -> Unit) {
        binding.ivAppLock.visibility = View.VISIBLE
        setOnClickListener(null)
        binding.ivAppLock.setOnClickListener { callback() }
    }

    fun unlock(callback: () -> Unit) {
        binding.ivAppLock.visibility = View.GONE
        setOnClickListener { callback() }
    }

    fun setIcon(drawable: Drawable?) {
        binding.ivAppIcon.setImageDrawable(drawable)
    }

    fun setLabel(label: String?) {
        binding.tvAppLabel.text = label
    }
}