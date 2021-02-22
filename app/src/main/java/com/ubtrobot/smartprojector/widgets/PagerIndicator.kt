package com.ubtrobot.smartprojector.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.compose.ui.graphics.Color
import androidx.core.view.children
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.utils.ResourceUtil
import kotlin.math.roundToInt

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

    fun setViewPager(vp: ViewPager) {
        removeAllViews()
        if (vp.adapter != null) {
            for (i in 0.until(vp.adapter!!.count)) {
                val item = View(context)
                addView(item)

                if (i == 0) {
                    item.background = resources.getDrawable(R.drawable.shape_circle_white)
                } else {
                    item.background = resources.getDrawable(R.drawable.shape_circle_overlay)
                }
                val itemLp = item.layoutParams as LinearLayout.LayoutParams
                val size = ResourceUtil.convertDpToPixel(12f, context).roundToInt()
                itemLp.width = size
                itemLp.height = size
                if (i > 0) {
                    itemLp.leftMargin = ResourceUtil.convertDpToPixel(30f, context).roundToInt()
                }

            }
        }
        vp.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                children.forEach { child ->
                    child.background = resources.getDrawable(R.drawable.shape_circle_overlay)
                }
                val item = getChildAt(position)
                if (item != null) {
                    item.background = resources.getDrawable(R.drawable.shape_circle_white)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
//        vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                children.forEach { child ->
//                    child.background = resources.getDrawable(R.drawable.shape_circle_overlay)
//                }
//                val item = getChildAt(position)
//                if (item != null) {
//                    item.background = resources.getDrawable(R.drawable.shape_circle_white)
//                }
//            }
//        })

    }
}