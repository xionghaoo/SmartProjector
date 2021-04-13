package com.ubtrobot.smartprojector.core

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.ubtrobot.smartprojector.utils.ResourceUtil
import kotlin.math.roundToInt

class ListDividerDecoration(
    private val lineHeight: Int,
    @ColorInt private val lineColor: Int,
    private val padding: Float = 0f,
    private val ignoreLastChildNum: Int = 1
) : RecyclerView.ItemDecoration() {

    companion object {
        fun general(context: Context?, color: Int = Color.argb(255, 221, 221, 221)) = ListDividerDecoration(
            lineHeight = ResourceUtil.convertDpToPixel(1f, context).roundToInt(),
            lineColor = color,
            ignoreLastChildNum = 0
        )
    }

    private val paint = Paint()

    init {
        paint.color = lineColor
        paint.isAntiAlias = true
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        parent.children.forEachIndexed { index, child ->
            if (index < (parent.childCount - ignoreLastChildNum)) {
                val childLp: RecyclerView.LayoutParams = child.layoutParams as RecyclerView.LayoutParams
                val top = child.bottom + childLp.bottomMargin
                c.drawRect(padding, top.toFloat(), parent.measuredWidth.toFloat() - padding, top + lineHeight.toFloat(), paint)
            }
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(0, 0, 0, lineHeight)
    }
}