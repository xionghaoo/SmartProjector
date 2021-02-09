package com.ubtrobot.smartprojector.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.compose.ui.graphics.Color
import androidx.core.view.children
import androidx.viewpager2.widget.ViewPager2
import com.ubtrobot.smartprojector.R

/**
 * viewpager指示器
 */
class PagerIndicator : LinearLayout {

    constructor(context: Context?) : super(context) {
        initial(context)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initial(context)
    }

    private fun initial(context: Context?) {
        orientation = HORIZONTAL
    }

    fun setViewPager(vp: ViewPager2) {
        removeAllViews()
        if (vp.adapter != null) {
            for (i in 0.until(vp.adapter!!.itemCount)) {
                val item = View(context)
                addView(item)
                item.background = resources.getDrawable(R.drawable.shape_circle_white)
                if (i == 0) {
                    item.background = resources.getDrawable(R.drawable.shape_circle_overlay)
                }
                val itemLp = item.layoutParams as LinearLayout.LayoutParams
                itemLp.width = 10
                itemLp.height = 10
                if (i > 0) {
                    itemLp.leftMargin = 50
                }

            }
        }
        vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                children.forEach { child ->
                    child.background = resources.getDrawable(R.drawable.shape_circle_overlay)
                }
                val item = getChildAt(position)
                if (item != null) {
                    item.background = resources.getDrawable(R.drawable.shape_circle_white)
                }
            }
        })

    }
}