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
import com.google.android.flexbox.FlexboxLayout
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

    enum class Type {
        MENU, APPS
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

    fun show(type: Type = Type.MENU) {
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
//        val content: LinearLayout = layoutInflater.inflate(R.layout.dialog_main_menu, null) as LinearLayout
//        bg.addView(content)
//        content.layoutParams.width = resources.getDimension(R.dimen._232dp).toInt()
//        content.layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT
        val content = contentView(type)
        content.scaleX = 0.6f
        content.scaleY = 0.6f
        content.alpha = 0f
        when (align) {
            Align.LEFT_TOP -> {
                content.x = iv.x - content.layoutParams.width - resources.getDimension(R.dimen._24dp)
                content.y = iv.y
            }
            Align.LEFT_BOTTOM -> {
                content.x = iv.x - content.layoutParams.width - resources.getDimension(R.dimen._24dp)
                content.y = iv.y + iv.layoutParams.height -
                    itemHeight(type) - content.paddingTop - content.paddingBottom
            }
            Align.RIGHT_TOP -> {
                content.x = iv.x + iv.layoutParams.width + resources.getDimension(R.dimen._24dp)
                content.y = iv.y
            }
            Align.RIGHT_BOTTOM -> {
                content.x = iv.x + iv.layoutParams.width + resources.getDimension(R.dimen._24dp)
                content.y = iv.y + iv.layoutParams.height -
                    itemHeight(type) - content.paddingTop - content.paddingBottom
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
            when (type) {
                Type.MENU -> {
                    addMenuItemView(content, data.icon, data.title, data.action)
                    if (index < listData.size - 1) {
                        addDivider(content)
                    }
                }
                Type.APPS -> {
                    addAppItemView(content, data.icon, data.title, data.action)
                }
            }
        }
    }

    private fun contentView(type: Type) : ViewGroup {
        return when (type) {
            Type.MENU -> {
                val content: ViewGroup = layoutInflater.inflate(R.layout.dialog_main_menu, null) as ViewGroup
                bg.addView(content)
                content.layoutParams.width = resources.getDimension(R.dimen._232dp).toInt()
                content.layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT
                content
            }
            Type.APPS -> {
                val content: ViewGroup = layoutInflater.inflate(R.layout.dialog_main_app_colletion, null) as ViewGroup
                bg.addView(content)
                content.layoutParams.width = resources.getDimension(R.dimen._764dp).toInt()
                content.layoutParams.height = appsRowNum() * resources.getDimension(R.dimen._150dp).toInt() +
                    resources.getDimension(R.dimen._28dp).toInt() * 2
                content
            }
        }
    }

    private fun itemHeight(type: Type) : Int {
        return when (type) {
            Type.MENU -> listData.size * resources.getDimension(R.dimen._78dp).toInt()
            Type.APPS -> appsRowNum() * resources.getDimension(R.dimen._150dp).toInt()
        }
    }

    private fun appsRowNum() : Int = if (listData.size % 4 == 0) listData.size / 4 else listData.size / 4 + 1

    private fun addAppItemView(container: ViewGroup, icon: Int, title: String?, action: HomeMenuAction) {
        val itemView = layoutInflater.inflate(R.layout.item_dialog_main_app, null)
        container.addView(itemView)
        itemView.layoutParams.width = resources.getDimension(R.dimen._174dp).toInt()
        itemView.layoutParams.height = resources.getDimension(R.dimen._150dp).toInt()
        itemView.findViewById<ImageView>(R.id.iv_dialog_menu_icon).setImageResource(icon)
        itemView.findViewById<TextView>(R.id.tv_dialog_menu_title).text = title
        itemView.setOnClickListener { action.invoke() }
    }

    private fun addMenuItemView(container: ViewGroup, icon: Int, title: String?, action: HomeMenuAction) {
        val itemView = layoutInflater.inflate(R.layout.item_dialog_main_menu, null)
        container.addView(itemView)
        itemView.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
        itemView.layoutParams.height = resources.getDimension(R.dimen._78dp).toInt()
        itemView.findViewById<ImageView>(R.id.iv_dialog_menu_icon).setImageResource(icon)
        itemView.findViewById<TextView>(R.id.tv_dialog_menu_title).text = title
        itemView.setOnClickListener { action.invoke() }
    }

    private fun addDivider(container: ViewGroup) {
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