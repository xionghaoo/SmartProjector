package com.ubtrobot.smartprojector.widgets

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.ubtrobot.smartprojector.databinding.WidgetLauncherToolbarBinding

class LauncherToolbar : FrameLayout {

    private lateinit var binding: WidgetLauncherToolbarBinding

    constructor(context: Context) : super(context) {
        initial(context)
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initial(context)
    }

    private fun initial(context: Context) {
        binding = WidgetLauncherToolbarBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun configBackButton(activity: Activity?) {
        binding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    fun setTitle(title: String?) {
        binding.tvTitle.text = title
    }

}