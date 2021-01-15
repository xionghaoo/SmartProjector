package com.ubtedu.alpha1x.ui.widgets;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.ubtedu.base.ui.R;

/**
 * @author Bright. Create on 2017/6/15.
 */
public class CompassView extends View {

    private static int DEFAULT_SIZE = 154;
    private Context mContext;
    private ValueAnimator mAnimator;
    private int mRotateDuration = 1000;
    private int mLineColor = 0xff2bf7fd;
    private int mTextColor = 0xFFA4BAC4;
    private Paint mPaint;
    private Paint mTextPaint;
    private Bitmap mBoundBitmap;
    private RectF mBoundRectF;
    private RectF mPointerRectF;
    private Bitmap mPointerBitmap;
    private String[] mDirectionText;
    private float mBoundRotateDegree = 0;
    private float mPointerRotateDegree = 0;
    private boolean isBoundRotate = true;

    public CompassView(Context context) {
        this(context, null);
    }

    public CompassView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompassView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mDirectionText = getResources().getStringArray(R.array.sensor_compass_directions);

        mPaint = new Paint();
        mPaint.setStrokeWidth(getResources().getDimension(R.dimen.ubt_dimen_4px));
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(mLineColor);

        // 下面两行代码防止Bitmap出现锯齿
        mPaint.setFilterBitmap(true);
        mPaint.setAntiAlias(true);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setStrokeWidth(1);
        mTextPaint.setTextSize(getResources().getDimension(R.dimen.ubt_dimen_34px));
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint.setColor(mTextColor);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        mBoundBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.sensor_compass, options);
        mPointerBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.sensor_compass_pointer, options);

        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(
                attrs, R.styleable.CompassView, defStyleAttr, 0);
        try {
            mBoundRotateDegree = typedArray.getFloat(R.styleable.CompassView_boundRotateDegree, 0);
            mPointerRotateDegree = typedArray.getFloat(R.styleable.CompassView_centerRotateDegree, 0);
        } catch (Exception e) {
            e.printStackTrace();
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
        // 画外框
        drawBound(canvas);
        // 画方向文字
        drawText(canvas);
        // 画中心指针圆
        drawPointer(canvas);
    }

    private void drawBound(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.save();
        canvas.rotate(180, getWidth() / 2, getHeight() / 2);
        canvas.rotate(mBoundRotateDegree, getWidth() / 2, getHeight() / 2);
        if (mBoundRectF == null) {
            mBoundRectF = new RectF(
                    0,
                    0,
                    getWidth(),
                    getHeight());
        }
        canvas.drawBitmap(mBoundBitmap, null, mBoundRectF, mPaint);
        canvas.restore();
    }

    private void drawText(Canvas canvas) {
        canvas.save();
        canvas.rotate(mBoundRotateDegree, getWidth() / 2, getHeight() / 2);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        for (int i = 0; i < 4; i++) {
            // 文字
            canvas.drawText(mDirectionText[i],
                    getWidth() / 2 - mTextPaint.measureText(mDirectionText[i]) / 2,
                    getHeight() * 98 / 570,  // 尺寸根据标注图作比例运算
                    mTextPaint);
            canvas.rotate(90, getWidth() / 2, getHeight() / 2);
        }
        canvas.restore();
    }

    private void drawPointer(Canvas canvas) {
        canvas.save();
        canvas.rotate(mPointerRotateDegree, getWidth() / 2, getHeight() / 2);
        if (mPointerRectF == null) {
            // 尺寸根据标注图作比例运算， 377是标注图中整个View宽度、高度，
            mPointerRectF = new RectF(getWidth() * 168 / 377, // 168是标注图中指针左侧距离View左边边距
                    getHeight() * 83 / 377, // 83是标注图中指针顶部距离View顶部边距
                    getWidth() * 209 / 377, // 109是标注图中指针右侧距离View左边边距
                    getHeight() * 294 / 377);   // 109是标注图中指针底部距离View顶部边距
        }
        canvas.drawBitmap(mPointerBitmap, null, mPointerRectF, mPaint);
        canvas.restore();
    }

    /**
     * 设置外框及原点、文字旋转角度
     */
    public void setBoundRotateDegree(float degree) {
        isBoundRotate = true;
        mBoundRotateDegree = -degree;
        postInvalidate();
    }

    /**
     * 设置中心指针旋转角度
     */
    public void setCenterRotateDegree(float degree) {
        isBoundRotate = false;
        mPointerRotateDegree = degree;
        postInvalidate();
    }

    /**
     * @return 获取当前角度
     */
    public float getCurrentDegree() {
        return isBoundRotate ? mBoundRotateDegree : mPointerRotateDegree;
    }

    /**
     * 设置旋转部分
     *
     * @param isBoundRotate true是外框和文字旋转，false是内圈旋转
     */
    public void setRotatePart(boolean isBoundRotate) {
        this.isBoundRotate = isBoundRotate;
    }

}
