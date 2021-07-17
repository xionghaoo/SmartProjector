package com.ubtrobot.smartprojector.widgets

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.WidgetSettingsSystemCardBinding
import java.lang.Exception

class SystemCardView : FrameLayout {

    enum class SystemType {
        INFANT, ELEMENTARY, SECONDARY
    }

    private var _binding: WidgetSettingsSystemCardBinding? = null
    private val binding get() = _binding!!

    constructor(context: Context) : super(context) {

    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        _binding = WidgetSettingsSystemCardBinding.inflate(LayoutInflater.from(context), this, true)

        binding.widgetSystemCardMark.visibility = View.GONE
        binding.widgetSystemCardOverlay.visibility = View.VISIBLE

        var ta: TypedArray? = null
        try {
            ta = context.theme.obtainStyledAttributes(attrs, R.styleable.SystemCardView, 0, 0)
            val type = SystemType.values()[ta.getInt(R.styleable.SystemCardView_scv_systemType, 0)]
            when (type) {
                SystemType.INFANT -> {
                    val lp = binding.widgetIvSystemCardRole.layoutParams
                    lp.width = resources.getDimension(R.dimen._136dp).toInt()
                    lp.height = resources.getDimension(R.dimen._250dp).toInt()
                    binding.widgetIvSystemCardRole.setImageResource(R.mipmap.ic_settings_system_infant)

                    binding.widgetTvSystemCardName.text = "幼儿系统"
                }
                SystemType.ELEMENTARY -> {
                    val lp = binding.widgetIvSystemCardRole.layoutParams
                    lp.width = resources.getDimension(R.dimen._233dp).toInt()
                    lp.height = resources.getDimension(R.dimen._331dp).toInt()
                    binding.widgetIvSystemCardRole.setImageResource(R.mipmap.ic_settings_system_elementary)

                    binding.widgetTvSystemCardName.text = "小学系统"
                }
                SystemType.SECONDARY -> {
                    val lp = binding.widgetIvSystemCardRole.layoutParams
                    lp.width = resources.getDimension(R.dimen._192dp).toInt()
                    lp.height = resources.getDimension(R.dimen._377dp).toInt()
                    binding.widgetIvSystemCardRole.setImageResource(R.mipmap.ic_settings_system_secondary)

                    binding.widgetTvSystemCardName.text = "中学系统"
                }
            }
        } catch (e: Exception) {
            ta?.recycle()
        }
    }

    fun enableSystem(enable: Boolean) {
        if (enable) {
            binding.widgetSystemCardMark.visibility = View.VISIBLE
            binding.widgetSystemCardOverlay.visibility = View.GONE
            binding.root.background = resources.getDrawable(R.drawable.shape_settings_bg_selected)
            binding.widgetTvSystemCardName.setTextColor(resources.getColor(R.color.color_system_card_selected))
        } else {
            binding.widgetSystemCardMark.visibility = View.GONE
            binding.widgetSystemCardOverlay.visibility = View.VISIBLE
            binding.root.background = resources.getDrawable(R.drawable.shape_settings_bg_normal)
            binding.widgetTvSystemCardName.setTextColor(resources.getColor(R.color.color_text_dark))
        }
    }
}