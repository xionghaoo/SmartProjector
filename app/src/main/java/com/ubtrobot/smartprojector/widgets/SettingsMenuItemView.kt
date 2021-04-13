package com.ubtrobot.smartprojector.widgets

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.databinding.WidgetSettingsMenuItemBinding
import kotlin.math.roundToInt

class SettingsMenuItemView : LinearLayout {

    private var binding: WidgetSettingsMenuItemBinding? = null

    constructor(context: Context) : super(context) {
    }
    constructor(context: Context, attrs: AttributeSet?) : super(
        context, attrs
    ) {
        binding = WidgetSettingsMenuItemBinding.inflate(LayoutInflater.from(context), this, true)

        orientation = VERTICAL

        var ta: TypedArray? = null
        try {
            ta = context.theme.obtainStyledAttributes(attrs, R.styleable.SettingsMenuItemView, 0, 0)
            val title = ta.getString(R.styleable.SettingsMenuItemView_smiv_title)
            binding?.tvSettingsMenuItemTitle?.text = title
        } catch (e: Exception) {
            ta?.recycle()
        }

        val line = TextView(context)
        addView(line)
        val lp = line.layoutParams as LinearLayout.LayoutParams
        lp.width = LinearLayout.LayoutParams.MATCH_PARENT
        lp.height = resources.getDimension(R.dimen._1dp).roundToInt()
        val sideMargin = resources.getDimension(R.dimen._40dp).roundToInt()
        lp.leftMargin = sideMargin
        lp.rightMargin = sideMargin
        line.setBackgroundColor(resources.getColor(R.color.color_menu_split))
    }

    fun setDetail(txt: String?) {
        binding?.tvSettingsMenuItemDetail?.text = txt
    }

    fun setOnSelectListener(call: () -> Unit) {
        binding?.root?.setOnClickListener { call() }
    }
}