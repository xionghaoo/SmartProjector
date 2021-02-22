package com.ubtrobot.smartprojector.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.ubtrobot.smartprojector.databinding.WidgetTuyaDeviceBinding

class TuyaDeviceView : FrameLayout {

    private lateinit var binding: WidgetTuyaDeviceBinding

    constructor(context: Context) : super(context) {
        initial(context)
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initial(context)
    }

    private fun initial(context: Context) {
        binding = WidgetTuyaDeviceBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setTitle(title: String?) {
        binding.tvDeviceName.text = title
    }

    fun setIcon(resId: Int) {
        binding.ivDevice.setImageResource(resId)
    }
}