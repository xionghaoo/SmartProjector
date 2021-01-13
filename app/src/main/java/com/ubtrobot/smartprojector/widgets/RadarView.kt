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

        private const val STEP = 2
        private const val CIRCLE_GAP = 40
        private const val DELAY = 50L
        private const val BASE_RADIUS = 100f
    }

    private var isStart: Boolean = false
    private var radius: Float = BASE_RADIUS
    private var radius2: Float = BASE_RADIUS
    private var runnable: Runnable? = null
    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        runnable = Runnable {
            if (isStart) {
                invalidate()
                postDelayed(runnable, DELAY)
            }
        }
    }

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

        val circleNum: Int = ((width / 2 - BASE_RADIUS) / CIRCLE_GAP).toInt()

//        for (i in 1..circleNum) {
//            var singleRadius = radius + i * CIRCLE_GAP
//            if (singleRadius >= width / 2) {
//                singleRadius = width / 2
//            }
//            paint.alpha = (255 * (width / 2 - singleRadius) / (width / 2 - BASE_RADIUS)).roundToInt()
//            canvas?.drawCircle(width / 2, height / 2, singleRadius, paint)
//        }
//        radius += SPEED

        radius += STEP
        if (radius >= width / 2) {
            radius = width / 2
        }
        drawCircle(canvas, radius, width, height)

        if (radius >= BASE_RADIUS + CIRCLE_GAP * 2 ) {
            Timber.d("draw circle 2")
            radius2 += STEP
            if (radius2 >= width / 2) {
                radius2 = width / 2
            }
            drawCircle(canvas, radius2, width, height)
        }

        if (radius >= width / 2) {
            radius = BASE_RADIUS
        }
        if (radius2 >= width / 2) {
            radius2 = BASE_RADIUS
        }

    }

    private fun drawCircle(canvas: Canvas?, radius: Float, width: Float, height: Float) {
        paint.alpha = (255 * (width / 2 - radius) / (width / 2 - BASE_RADIUS)).roundToInt()
        canvas?.drawCircle(width / 2, height / 2, radius, paint)
    }

    fun start() {
        removeCallbacks(runnable)
        isStart = true
        postDelayed(runnable, DELAY)
    }

    fun stop() {
        isStart = false
        removeCallbacks(runnable)
    }
}