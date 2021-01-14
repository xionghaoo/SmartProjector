package com.ubtrobot.smartprojector.keyboard.layouts

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.ubtrobot.smartprojector.R
import com.ubtrobot.smartprojector.keyboard.KeyboardListener
import com.ubtrobot.smartprojector.keyboard.controllers.KeyboardController
import com.ubtrobot.smartprojector.utils.ResourceUtil
import java.lang.Exception
import kotlin.math.roundToInt

/**
 * 数字键盘
 */
class NumberKeyboardView : LinearLayout {

    enum class Type {
        NUMBER, PHONE, CASH, POP
    }

    companion object {
    }

    private var onConfirm: (() -> Unit)? = null
    private var onAmountClick: (() -> Unit)? = null
//    private var spacerWidth = 10f
    private var spacerWidth = 10f
    private var keyHeight = 60f
    private var popKeyHeight = 50f

    private var tvAmount: TextView? = null

    constructor(context: Context) : super(context) {
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        orientation = LinearLayout.VERTICAL
        spacerWidth = ResourceUtil.convertDpToPixel(15f, context)
        keyHeight = ResourceUtil.convertDpToPixel(60f, context)
        popKeyHeight = ResourceUtil.convertDpToPixel(50f, context)

        var ta: TypedArray? = null
        try {
            ta = context.theme.obtainStyledAttributes(attrs, R.styleable.NumberKeyboardView, 0, 0)
            val keyboardWidth = ta.getDimension(R.styleable.NumberKeyboardView_nkv_width, ResourceUtil.convertDpToPixel(500f, context))
            val type = Type.values()[ta.getInt(R.styleable.NumberKeyboardView_nkv_type, 0)]
            when (type) {
                Type.NUMBER -> createNumberKeyboard(keyboardWidth)
                Type.PHONE -> createPhoneKeyboard(keyboardWidth)
                Type.CASH -> createCashKeyboard(keyboardWidth)
                Type.POP -> createPopKeyboard(keyboardWidth)
            }

            if (type != Type.POP) {
                setPadding(
                    ResourceUtil.convertDpToPixel(15f, context).toInt(),
                    ResourceUtil.convertDpToPixel(15f, context).toInt(),
                    ResourceUtil.convertDpToPixel(15f, context).toInt(),
                    ResourceUtil.convertDpToPixel(15f, context).toInt()
                )
            }

        } catch (e: Exception) {
            ta?.recycle()
        }
    }

    private var controller: KeyboardController? = null

    private fun createNumberKeyboard(screenWidth: Float) {
        val keyWidth = (screenWidth - spacerWidth * 3 - ResourceUtil.convertDpToPixel(15f, context) * 2) / 4
        val rowOne = LinearLayout(context)
        rowOne.orientation = HORIZONTAL
        createKeyView("1", '1', rowOne, width = keyWidth)
        createKeyView("2", '2', rowOne, width = keyWidth)
        createKeyView("3", '3', rowOne, width = keyWidth)
        createKeyView("删除", KeyboardController.SpecialKey.DELETE, rowOne, width = keyWidth, marginEnd = 0f)
        addView(rowOne)

        createSpacer(this, height = spacerWidth)

        val columnContainer = LinearLayout(context)
        columnContainer.orientation = HORIZONTAL

        val columnOne = LinearLayout(context)
        columnOne.orientation = VERTICAL

        val rowTwo = LinearLayout(context)
        rowTwo.orientation = HORIZONTAL
        createKeyView("4", '4', rowTwo, width = keyWidth)
        createKeyView("5", '5', rowTwo, width = keyWidth)
        createKeyView("6", '6', rowTwo, width = keyWidth)
        columnOne.addView(rowTwo)

        createSpacer(columnOne, height = spacerWidth)

        val rowThree = LinearLayout(context)
        rowThree.orientation = HORIZONTAL
        createKeyView("7", '7', rowThree, width = keyWidth)
        createKeyView("8", '8', rowThree, width = keyWidth)
        createKeyView("9", '9', rowThree, width = keyWidth)
        columnOne.addView(rowThree)

        createSpacer(columnOne, height = spacerWidth)

        val rowFour = LinearLayout(context)
        rowFour.orientation = HORIZONTAL
        createKeyView("0", '0', rowFour, width = keyWidth)
        createKeyView("00", KeyboardController.SpecialKey.TWO_ZERO, rowFour, width = keyWidth)
        createKeyView(".", '.', rowFour, width = keyWidth)
        columnOne.addView(rowFour)

        columnContainer.addView(columnOne)

        createKeyView("确定", KeyboardController.SpecialKey.FORWARD, columnContainer, width = keyWidth, height = spacerWidth * 2 + keyHeight * 3)

        addView(columnContainer)

    }

    private fun createPhoneKeyboard(screenWidth: Float) {
        val keyWidth = (screenWidth - spacerWidth * 3 - ResourceUtil.convertDpToPixel(15f, context) * 2) / 4
        val rowOne = LinearLayout(context)
        rowOne.orientation = HORIZONTAL
        createKeyView("1", '1', rowOne, width = keyWidth)
        createKeyView("2", '2', rowOne, width = keyWidth)
        createKeyView("3", '3', rowOne, width = keyWidth)
        createKeyView("清空", KeyboardController.SpecialKey.CLEAR, rowOne, width = keyWidth, marginEnd = 0f)
        addView(rowOne)

        createSpacer(this, height = spacerWidth)

        val rowTwo = LinearLayout(context)
        rowTwo.orientation = HORIZONTAL
        createKeyView("4", '4', rowTwo, width = keyWidth)
        createKeyView("5", '5', rowTwo, width = keyWidth)
        createKeyView("6", '6', rowTwo, width = keyWidth)
        createKeyView("删除", KeyboardController.SpecialKey.DELETE, rowTwo, width = keyWidth, marginEnd = 0f)
        addView(rowTwo)

        createSpacer(this, height = spacerWidth)

        val columnContainer = LinearLayout(context)
        columnContainer.orientation = HORIZONTAL

        val columnOne = LinearLayout(context)
        columnOne.orientation = VERTICAL

        val rowThree = LinearLayout(context)
        rowThree.orientation = HORIZONTAL
        createKeyView("7", '7', rowThree, width = keyWidth)
        createKeyView("8", '8', rowThree, width = keyWidth)
        createKeyView("9", '9', rowThree, width = keyWidth)
        columnOne.addView(rowThree)

        createSpacer(columnOne, height = spacerWidth)

        val rowFour = LinearLayout(context)
        rowFour.orientation = HORIZONTAL
        createKeyView("0", '0', rowFour, width = keyWidth * 3 + spacerWidth * 2)
        columnOne.addView(rowFour)

        columnContainer.addView(columnOne)

        createKeyView("确定", KeyboardController.SpecialKey.FORWARD, columnContainer, width = keyWidth, height = spacerWidth + keyHeight * 2)

        addView(columnContainer)

    }

    private fun createCashKeyboard(screenWidth: Float) {
        val keyWidth = (screenWidth - spacerWidth * 3 - ResourceUtil.convertDpToPixel(15f, context) * 2) / 4
        val rowOne = LinearLayout(context)
        rowOne.orientation = HORIZONTAL
        createKeyView("1", '1', rowOne, width = keyWidth)
        createKeyView("2", '2', rowOne, width = keyWidth)
        createKeyView("3", '3', rowOne, width = keyWidth)
        createKeyView("￥0.00", KeyboardController.SpecialKey.AMOUNT, rowOne, width = keyWidth, marginEnd = 0f)
        addView(rowOne)

        createSpacer(this, height = spacerWidth)

        val rowTwo = LinearLayout(context)
        rowTwo.orientation = HORIZONTAL
        createKeyView("4", '4', rowTwo, width = keyWidth)
        createKeyView("5", '5', rowTwo, width = keyWidth)
        createKeyView("6", '6', rowTwo, width = keyWidth)
        createKeyView("删除", KeyboardController.SpecialKey.DELETE, rowTwo, width = keyWidth, marginEnd = 0f)
        addView(rowTwo)

        createSpacer(this, height = spacerWidth)

        val columnContainer = LinearLayout(context)
        columnContainer.orientation = HORIZONTAL

        val columnOne = LinearLayout(context)
        columnOne.orientation = VERTICAL

        val rowThree = LinearLayout(context)
        rowThree.orientation = HORIZONTAL
        createKeyView("7", '7', rowThree, width = keyWidth)
        createKeyView("8", '8', rowThree, width = keyWidth)
        createKeyView("9", '9', rowThree, width = keyWidth)
        columnOne.addView(rowThree)

        createSpacer(columnOne, height = spacerWidth)

        val rowFour = LinearLayout(context)
        rowFour.orientation = HORIZONTAL
        createKeyView("0", '0', rowFour, width = keyWidth)
        createKeyView("00", KeyboardController.SpecialKey.TWO_ZERO, rowFour, width = keyWidth)
        createKeyView(".", '.', rowFour, width = keyWidth)
        columnOne.addView(rowFour)

        columnContainer.addView(columnOne)

        createKeyView(
            "确定",
            KeyboardController.SpecialKey.FORWARD,
            columnContainer, width = keyWidth,
            height = spacerWidth + keyHeight * 2,
            txtColor = R.color.white,
            bgRes = R.drawable.selector_confirm_cash_btn
        )

        addView(columnContainer)

    }

    private fun createPopKeyboard(screenWidth: Float) {
        val keyWidth = screenWidth / 3
        val rowOne = LinearLayout(context)
        rowOne.orientation = HORIZONTAL
        createKeyView("1", '1', rowOne, width = keyWidth, height = popKeyHeight, marginEnd = 0f, isPop = true)
        createKeyView("2", '2', rowOne, width = keyWidth, height = popKeyHeight, marginEnd = 0f, isPop = true)
        createKeyView("3", '3', rowOne, width = keyWidth, height = popKeyHeight, marginEnd = 0f, isPop = true)
//        createKeyView("￥0.00", KeyboardController.SpecialKey.AMOUNT, rowOne, width = keyWidth, marginEnd = 0f)
        addView(rowOne)

//        createSpacer(this, height = spacerWidth)

        val rowTwo = LinearLayout(context)
        rowTwo.orientation = HORIZONTAL
        createKeyView("4", '4', rowTwo, width = keyWidth, height = popKeyHeight, marginEnd = 0f, isPop = true)
        createKeyView("5", '5', rowTwo, width = keyWidth, height = popKeyHeight, marginEnd = 0f, isPop = true)
        createKeyView("6", '6', rowTwo, width = keyWidth, height = popKeyHeight, marginEnd = 0f, isPop = true)
//        createKeyView("delete", KeyboardController.SpecialKey.DELETE, rowTwo, width = keyWidth, marginEnd = 0f)
        addView(rowTwo)

        val rowThree = LinearLayout(context)
        rowThree.orientation = HORIZONTAL
        createKeyView("7", '7', rowThree, width = keyWidth, height = popKeyHeight, marginEnd = 0f, isPop = true)
        createKeyView("8", '8', rowThree, width = keyWidth, height = popKeyHeight, marginEnd = 0f, isPop = true)
        createKeyView("9", '9', rowThree, width = keyWidth, height = popKeyHeight, marginEnd = 0f, isPop = true)
        addView(rowThree)

        val rowFour = LinearLayout(context)
        rowFour.orientation = HORIZONTAL
        createKeyView(".", '.', rowFour, width = keyWidth, height = popKeyHeight, marginEnd = 0f, isPop = true)
        createKeyView("0", '0', rowFour, width = keyWidth, height = popKeyHeight, marginEnd = 0f, isPop = true)
        createKeyView("删除", KeyboardController.SpecialKey.DELETE, rowFour, width = keyWidth, height = popKeyHeight, marginEnd = 0f, bgRes = R.drawable.selector_pop_number_key)
        addView(rowFour)
    }

    fun setController(_controller: KeyboardController) {
        controller = _controller
        controller?.registerListener(object : KeyboardListener {
            override fun characterClicked(c: Char) {
            }

            override fun specialKeyClicked(key: KeyboardController.SpecialKey) {
                when(key) {
                    KeyboardController.SpecialKey.TWO_ZERO -> {
                        controller?.addCharacter('0')
                        controller?.addCharacter('0')
                    }
                    KeyboardController.SpecialKey.FORWARD -> {
                        onConfirm?.invoke()
                    }
                    KeyboardController.SpecialKey.AMOUNT -> {
                        onAmountClick?.invoke()
                    }
                }
            }
        })
    }

    fun setConfirmListener(callback: () -> Unit) {
        onConfirm = callback
    }

//    fun setAmount(amount: Int) {
//        tvAmount?.text = "￥${amount.toYuan()}"
//    }

    fun setAmountKeyClickListener(callback: () -> Unit) {
        onAmountClick = callback
    }

    private fun createKeyView(
        txt: String?,
        c: Char,
        container: ViewGroup,
        width: Float? = null,
        height: Float? = null,
        marginStart: Float? = null,
        marginEnd: Float? = null,
        isPop: Boolean = false
    ) {
        val tv = TextView(context)
        tv.text = txt
        tv.background = context.getDrawable(
            if (isPop) {
                R.drawable.selector_pop_number_key
            } else {
                R.drawable.selector_number_key
            }
        )
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        tv.setTypeface(null, Typeface.BOLD)
        tv.setTextColor(context.resources.getColor(R.color.color_222222))
        tv.gravity = Gravity.CENTER
//        tv.setTypeface(null, Typeface.BOLD)
        container.addView(tv)
        val lp = tv.layoutParams as LinearLayout.LayoutParams
        lp.width = width?.toInt() ?: ResourceUtil.convertDpToPixel(50f, context).toInt()
        lp.height = (height ?: keyHeight).roundToInt()
        lp.marginEnd = marginEnd?.toInt() ?: spacerWidth.toInt()
        lp.marginStart = marginEnd?.toInt() ?: 0

        tv.setOnClickListener {
            controller?.onKeyStroke(c)
        }
    }

    private fun createKeyView(
        txt: String?,
        key: KeyboardController.SpecialKey,
        container: ViewGroup,
        width: Float? = null,
        height: Float? = null,
        marginStart: Float? = null,
        marginEnd: Float? = null,
//        isPop: Boolean = false,
        txtColor: Int = R.color.color_222222,
        bgRes: Int = R.drawable.selector_number_key
    ) {
        val tv = TextView(context)
        tv.text = txt
//        tv.background = context.getDrawable(
//            if (isPop) {
//                R.drawable.selector_pop_number_key
//            } else {
//                R.drawable.selector_number_key
//            }
//        )
        tv.background = context.getDrawable(bgRes)
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        tv.setTypeface(null, Typeface.BOLD)
        if (key == KeyboardController.SpecialKey.AMOUNT) {
            tvAmount = tv
        }
        tv.setTextColor(context.resources.getColor(txtColor))
        tv.gravity = Gravity.CENTER
        container.addView(tv)
        val lp = tv.layoutParams as LinearLayout.LayoutParams
        lp.width = width?.toInt() ?: ResourceUtil.convertDpToPixel( 50f, context).toInt()
        lp.height = (height ?: keyHeight).roundToInt()
        lp.marginEnd = marginEnd?.toInt() ?: spacerWidth.toInt()
        lp.marginStart = marginEnd?.toInt() ?: 0

        tv.setOnClickListener {
            controller?.onKeyStroke(key)
        }
    }

    private fun createSpacer(container: ViewGroup, width: Float? = null, height: Float? = null) {
        val tv = TextView(context)
        container.addView(tv)
        val lp = tv.layoutParams as LinearLayout.LayoutParams
        lp.width = width?.toInt() ?: LinearLayout.LayoutParams.MATCH_PARENT
        lp.height = height?.toInt() ?: LinearLayout.LayoutParams.MATCH_PARENT
    }
}