package com.ubtedu.alpha1x.ui.widgets;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.ubtedu.base.ui.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Bright. Create on 2017/6/15.
 */
public class LoadingView extends View {

    private static int DEFAULT_SIZE = 154;
    private Context mContext;
    private int mBitmapResID;
    private Bitmap mBitmap;
    private int mRotateDegree = 0;
    private Paint mPaint;
    private ValueAnimator mAnimator;
    private int mBlockWidth = 0, mBlockHeight = 0;
    private int mBlockCount = 8;
    private int[] mAlphas = new int[]{255, 204, 153, 102, 51, 51, 51, 51};
    private int mDuration = 1000;
    private int mBlockColor = 0xff2bf7fd;
    private Rect mBlockRect;
    private Timer mTimer;
    private TimerTask mTimerTask;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(
                attrs, R.styleable.LoadingView, defStyleAttr, 0);
        try {
            mDuration = typedArray.getInteger(R.styleable.LoadingView_duration, mDuration);
            mBlockColor = typedArray.getColor(R.styleable.LoadingView_blockColor, mBlockColor);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            typedArray.recycle();
        }

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(mBlockColor);

        startAnim();
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

        if (mBlockWidth == 0) {
            mBlockWidth = getWidth() * 14 / 231;
        }
        if (mBlockHeight == 0) {
            mBlockHeight = getWidth() * 38 / 231;
        }

        canvas.save();
        canvas.rotate(mRotateDegree, getWidth() / 2, getHeight() / 2);
        drawIndicateBlock(canvas);
        canvas.restore();
    }

    private void drawIndicateBlock(Canvas canvas) {
        canvas.save();
        if (mBlockRect == null) {
            mBlockRect = new Rect(getWidth() / 2 - mBlockWidth / 2,
                    getHeight() - mBlockHeight,
                    getWidth() / 2 + mBlockWidth / 2,
                    getHeight());
        }
        for (int i = 0; i < mBlockCount; i++) {
            if (mAlphas.length == mBlockCount) {
                mPaint.setAlpha(mAlphas[i]);
            }
            canvas.drawRect(mBlockRect, mPaint);
            canvas.rotate(360 / (mBlockCount), getWidth() / 2, getHeight() / 2);
        }
        canvas.restore();
    }

    public void startAnim() {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                mRotateDegree += 360 / (mBlockCount);
                if (mRotateDegree >= 360) {
                    mRotateDegree = 0;
                }
                postInvalidate();
            }
        };
        mTimer.schedule(mTimerTask, 0, mDuration / mBlockCount);
    }

    public void stopAnim() {
        if (mTimer != null) {
            mTimerTask.cancel();
            mTimer.cancel();
            mTimerTask = null;
            mTimer = null;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }
}
