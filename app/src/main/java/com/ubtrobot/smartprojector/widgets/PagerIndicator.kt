package com.ubtrobot.smartprojector.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.viewpager.widget.ViewPager
import com.ubtrobot.smartprojector.R
import kotlin.math.roundToInt

/**
 * viewpager指示器
 */
class PagerIndicator : LinearLayout {

    private var currentSelectedPage: Int = 0

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
        if (vp.adapter != null) {
            updateItems(vp.adapter!!.count)
        }
        vp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//                Timber.d("position: $position, offset: $positionOffset, positionOffsetPixels: $positionOffsetPixels")
            }

            override fun onPageSelected(position: Int) {
                currentSelectedPage = position
                children.forEach { child ->
                    child.background = resources.getDrawable(R.drawable.shape_indicator_normal)
                }
                val item = getChildAt(position)
                if (item != null) {
                    item.background = resources.getDrawable(R.drawable.shape_indicator_highlight)
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

    fun updateItems(pageCount: Int) {
        removeAllViews()
        for (i in 0.until(pageCount)) {
            val item = View(context)
            addView(item)

            if (i == currentSelectedPage) {
                item.background = resources.getDrawable(R.drawable.shape_indicator_highlight)
            } else {
                item.background = resources.getDrawable(R.drawable.shape_indicator_normal)
            }
            val itemLp = item.layoutParams as LinearLayout.LayoutParams
            val size = resources.getDimension(R.dimen._12dp).roundToInt()
            itemLp.width = size
            itemLp.height = size
            if (i > 0) {
                itemLp.leftMargin = resources.getDimension(R.dimen._30dp).roundToInt()
            }

        }
    }
}