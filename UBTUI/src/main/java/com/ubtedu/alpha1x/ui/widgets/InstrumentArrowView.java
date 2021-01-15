package com.ubtedu.alpha1x.ui.widgets;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;

import com.ubtedu.base.ui.R;

/**
 * @author Bright. Create on 2017/6/15.
 */
public class InstrumentArrowView extends View {

    private static int DEFAULT_SIZE = 154;
    private Context mContext;
    private ValueAnimator mAnimator;
    private Paint mPaint;

    // 白圆半径
    private float mCenterCircleRadius = 0;
    // 黑圆半径
    private float mCenterDotRadius = 0;
    // 指针圆心距离控件最左端距离
    private float mCenterMarginLeft = 0;
    // 指针圆心距离控件最顶端端距离
    private float mCenterMarginTop = 0;

    // 指针最大宽度
    private float mArrowWidth = 0;
    // 指针高度
    private float mArrowHeight = 0;
    // 指针颜色
    private int mArrowColor;

    // 指针三角形路径
    private Path mArrowPath;

    private float mRotation = 0;

    public InstrumentArrowView(Context context) {
        this(context, null);
    }

    public InstrumentArrowView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InstrumentArrowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(getResources().getDimension(R.dimen.ubt_dimen_4px));
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(
                attrs, R.styleable.InstrumentArrowView, defStyleAttr, 0);
        try {
            mCenterCircleRadius = typedArray.getDimension(R.styleable.InstrumentArrowView_centerCircleRadius,
                    getResources().getDimension(R.dimen.ubt_dimen_7px));
            mCenterDotRadius = typedArray.getDimension(R.styleable.InstrumentArrowView_centerDotRadius,
                    getResources().getDimension(R.dimen.ubt_dimen_32px));
            mCenterMarginLeft = typedArray.getDimension(R.styleable.InstrumentArrowView_centerMarginLeft,
                    getResources().getDimension(R.dimen.ubt_dimen_375px));
            mCenterMarginTop = typedArray.getDimension(R.styleable.InstrumentArrowView_centerMarginTop,
                    getResources().getDimension(R.dimen.ubt_dimen_366px));
            mArrowWidth = typedArray.getDimension(R.styleable.InstrumentArrowView_arrowWidth,
                    getResources().getDimension(R.dimen.ubt_dimen_24px));
            mArrowHeight = typedArray.getDimension(R.styleable.InstrumentArrowView_arrowHeight,
                    getResources().getDimension(R.dimen.ubt_dimen_240px));
            mArrowColor = typedArray.getColor(R.styleable.InstrumentArrowView_arrowColor, 0xff3ef7fc);
            mRotation = typedArray.getFloat(R.styleable.InstrumentArrowView_arrowRotation, 0f);
        } catch (Exception e) {
            Log.e("Alpha1X", "[InstrumentArrowView] Error: " + e.getMessage());
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth, measureHeight;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            // 具体的值和match_parent
            measureWidth = widthSize;
        } else {
            // wrap_content
            measureWidth = DEFAULT_SIZE;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            measureHeight = heightSize;
        } else {
            measureHeight = DEFAULT_SIZE;
        }
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画中心指针
        drawArrow(canvas);

        // 画中心白圆
        drawCenterCircle(canvas);

        // 画中心黑点
        drawCenterDot(canvas);
    }

    private void drawCenterCircle(Canvas canvas) {
        canvas.save();
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(mCenterMarginLeft, mCenterMarginTop, mCenterCircleRadius, mPaint);
        canvas.restore();
    }

    private void drawCenterDot(Canvas canvas) {
        canvas.save();
        mPaint.setColor(Color.BLACK);
        canvas.drawCircle(mCenterMarginLeft, mCenterMarginTop, mCenterDotRadius, mPaint);
        canvas.restore();
    }

    /**
     * 画指针，填充三角形
     */
    private void drawArrow(Canvas canvas) {
        canvas.save();
        canvas.rotate(mRotation, mCenterMarginLeft, mCenterMarginTop);
        if (mArrowPath == null) {
            mArrowPath = new Path();
            mArrowPath.moveTo(mCenterMarginLeft - mArrowWidth / 2, mCenterMarginTop);
            mArrowPath.lineTo(mCenterMarginLeft + mArrowWidth / 2, mCenterMarginTop);
            mArrowPath.lineTo(mCenterMarginLeft, mCenterMarginTop - mArrowHeight);
            mArrowPath.close();
        }
        mPaint.setColor(mArrowColor);
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(mArrowPath, mPaint);
        canvas.restore();
    }

    /**
     * 设置指针旋转角，指针指向正上方为0度，指向左侧为负数，指向右侧为正数。
     */
    public void setArrowRotation(float rotation) {
        mAnimator = ValueAnimator.ofFloat(mRotation, rotation);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRotation = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.setDuration(500);
        mAnimator.start();
    }

    public float getArrowRotation() {
        return mRotation;
    }

}
