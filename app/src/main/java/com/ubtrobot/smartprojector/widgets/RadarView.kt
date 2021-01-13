package com.ubtrobot.smartprojector.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import timber.log.Timber
import kotlin.math.roundToInt

class RadarView : View {

    companion object {
        private var WRAP_WIDTH = 200
        private var WRAP_HEIGHT = 200

        private const val STEP = 10
        private const val CIRCLE_GAP = 100
        private const val DELAY = 60L
        private const val MIN_RADIUS = 100f
    }

    private var isStart: Boolean = false
    private var currentMaxRadius: Float = MIN_RADIUS
//    private var runnable: Runnable? = null
    private var circleNum: Int = 1
    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

//    init {
//        runnable = Runnable {
//            if (isStart) {
//                invalidate()
//                postDelayed(runnable, DELAY)
//            }
//        }
//    }

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.color = resources.getColor(android.R.color.holo_blue_light)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(WRAP_WIDTH, WRAP_HEIGHT)
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(WRAP_WIDTH, heightSize)
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize, WRAP_HEIGHT)
        } else {
            setMeasuredDimension(widthSize, heightSize)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val topPadding = paddingTop
        val bottomPadding = paddingBottom
        val leftPadding = paddingLeft
        val rightPadding = paddingRight
        val width = (width - leftPadding - rightPadding).toFloat()
        val height = (height - topPadding - bottomPadding).toFloat()
        val maxRadius = width / 2
        circleNum = ((maxRadius - MIN_RADIUS) / CIRCLE_GAP).toInt()

        if (currentMaxRadius > maxRadius) {
            currentMaxRadius -= CIRCLE_GAP
        }

        Timber.d("circle num = $circleNum")
        // 以一定步长增加半径值
        currentMaxRadius += STEP
        // 根据当前半径绘制剩余圆形
        for (i in 0..circleNum) {
            val r = currentMaxRadius - (i + 1) * CIRCLE_GAP
            // 大于最小显示半径的圆才能绘制出来
            if (r > MIN_RADIUS) {
//                drawCircle(canvas, r, width, height)
                paint.alpha = (255 * (maxRadius - r) / (maxRadius - MIN_RADIUS)).roundToInt()
                canvas?.drawCircle(width / 2, height / 2, r, paint)
            }
        }

        if (isStart) {
            postInvalidateDelayed(DELAY)
        }
    }

    private fun drawCircle(canvas: Canvas?, radius: Float, width: Float, height: Float) {
        val maxRadius = width / 2
        paint.alpha = (255 * (maxRadius - radius) / (maxRadius - MIN_RADIUS)).roundToInt()
        canvas?.drawCircle(width / 2, height / 2, radius, paint)
    }

    fun start() {
//        removeCallbacks(runnable)
        isStart = true
        invalidate()
//        postDelayed(runnable, DELAY)
    }

    fun stop() {
        isStart = false
//        removeCallbacks(runnable)
    }
}