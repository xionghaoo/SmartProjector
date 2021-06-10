package com.ubtrobot.smartprojector.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.ubtrobot.smartprojector.R
import jp.wasabeef.blurry.Blurry
import kotlin.math.roundToInt

typealias HomeMenuAction = () -> Unit

data class HomeMenuData(
    val icon: Int,
    val title: String,
    val action: () -> Unit
)

class HomeMenuDialog(
    private val context: Context,
    private val rootView: ViewGroup,
    private val target: View,
    private var align: Align = Align.LEFT_TOP,
    private val listData: ArrayList<HomeMenuData>
) {

    enum class Align {
        LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM
    }

    private val layoutInflater = LayoutInflater.from(context)
    private val resources = context.resources
    private val bg: FrameLayout

    init {
        Blurry.with(context)
            .radius(25)
            .sampling(2)
            .animate(100)
            .onto(rootView)

        // 添加背景
        bg = FrameLayout(context)
        bg.setOnClickListener {
            rootView.removeView(bg)
            Blurry.delete(rootView)
        }
        rootView.addView(bg)
        bg.layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT
        bg.layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT
    }

    fun show() {
        // 提取目图片
        val iv = ImageView(context)
        iv.isFocusable = true
        iv.isClickable = true
        bg.addView(iv)
        iv.layoutParams.width = target.width
        iv.layoutParams.height = target.height
        iv.setImageBitmap(loadBitmapFromView(target))
        val arr = IntArray(2)
        target.getLocationInWindow(arr)
        iv.x = arr[0].toFloat()
        iv.y = arr[1].toFloat()
        iv.animate()
            .scaleX(1.05f)
            .scaleY(1.05f)
            .setDuration(100)
            .start()

        // 显示内容
        val content: LinearLayout = layoutInflater.inflate(R.layout.dialog_main_menu, null) as LinearLayout
        content.scaleX = 0.5f
        content.scaleY = 0.5f
        content.alpha = 0f
        bg.addView(content)
        content.layoutParams.width = resources.getDimension(R.dimen._232dp).toInt()
        content.layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT
        when (align) {
            Align.LEFT_TOP -> {
                content.x = iv.x - content.layoutParams.width - resources.getDimension(R.dimen._24dp)
                content.y = iv.y
            }
            Align.LEFT_BOTTOM -> {
                content.x = iv.x - content.layoutParams.width - resources.getDimension(R.dimen._24dp)
                content.y = iv.y + iv.layoutParams.height -
                    listData.size * resources.getDimension(R.dimen._78dp).toInt() -
                    content.paddingTop - content.paddingBottom
            }
            Align.RIGHT_TOP -> {
                content.x = iv.x + iv.layoutParams.width + resources.getDimension(R.dimen._24dp)
                content.y = iv.y
            }
            Align.RIGHT_BOTTOM -> {
                content.x = iv.x + iv.layoutParams.width + resources.getDimension(R.dimen._24dp)
                content.y = iv.y + iv.layoutParams.height -
                    listData.size * resources.getDimension(R.dimen._78dp).toInt() -
                    content.paddingTop - content.paddingBottom
            }
        }
        content.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(100)
            .start()

        // 添加选择项
        listData.forEachIndexed { index, data ->
            addItemView(content, data.icon, data.title, data.action)
            if (index < listData.size - 1) {
                addDivider(content)
            }
        }
    }

    private fun addItemView(container: LinearLayout, icon: Int, title: String?, action: HomeMenuAction) {
        val itemView = layoutInflater.inflate(R.layout.item_dialog_main_menu, null)
        container.addView(itemView)
        itemView.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
        itemView.layoutParams.height = resources.getDimension(R.dimen._78dp).toInt()
        itemView.findViewById<ImageView>(R.id.iv_dialog_menu_icon).setImageResource(icon)
        itemView.findViewById<TextView>(R.id.tv_dialog_menu_title).text = title
        itemView.setOnClickListener { action.invoke() }
    }

    private fun addDivider(container: LinearLayout) {
        val divider = View(context)
        container.addView(divider)
        divider.setBackgroundColor(resources.getColor(R.color.color_DDDDDD))
        val lp = divider.layoutParams as LinearLayout.LayoutParams
        lp.width = LinearLayout.LayoutParams.MATCH_PARENT
        lp.height = resources.getDimension(R.dimen._1dp).roundToInt()
        lp.leftMargin = resources.getDimension(R.dimen._64dp).roundToInt()
    }

    private fun loadBitmapFromView(v: View): Bitmap? {
        val b = Bitmap.createBitmap(
            v.width,
            v.height,
            Bitmap.Config.ARGB_8888
        )
        val c = Canvas(b)
        v.layout(v.left, v.top, v.right, v.bottom)
        v.draw(c)
        return b
    }
}