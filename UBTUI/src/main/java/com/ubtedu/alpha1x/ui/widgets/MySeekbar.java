package com.ubtedu.alpha1x.ui.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.ubtedu.base.ui.R;

/**
 * Created by qinicy on 2017/6/15.
 */

public class MySeekbar extends androidx.appcompat.widget.AppCompatSeekBar {
    private Paint mPaint;

    private int mTextSize;
    private int mTextColor;
    private int mFillColor;
    private int mTextViewWidth;
    private int mTextViewHeight;

    public MySeekbar(Context context) {
        super(context);
        init();
    }

    public MySeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MySeekbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        int defaultTextSize = getResources().getDimensionPixelOffset(R.dimen.ubt_font_34px);
//        mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, defaultTextSize, getResources().getDisplayMetrics());

        mTextViewWidth = getResources().getDimensionPixelOffset(R.dimen.ubt_font_60px);
        mTextViewHeight = getResources().getDimensionPixelOffset(R.dimen.ubt_font_46px);

        mTextSize = getResources().getDimensionPixelOffset(R.dimen.ubt_font_34px);
        mTextColor = Color.WHITE;
        mFillColor = Color.parseColor("#2AC1BF");

        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setTextSize(mTextSize);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        String progressText = "" + this.getProgress();


        Rect thumbBound = getThumb().getBounds();

        float startX = thumbBound.exactCenterX() - mTextViewWidth / 2;
//        float startY = - thumbBound.height() / 3 * 5;
        float startY = -20;
        RectF frame = new RectF(startX, startY, startX + mTextViewWidth, startY+mTextViewHeight);

        mPaint.setColor(mFillColor);
        canvas.drawRect(frame, mPaint);
        mPaint.setColor(mTextColor);
        Rect mBound = new Rect();
        mPaint.getTextBounds(progressText, 0, progressText.length(), mBound);
        canvas.drawText(progressText, startX + frame.width() / 2 - mBound.width() / 2, startY+frame.height() / 2 + mBound.height() / 2, mPaint);
    }

    public void setTextSize(int textSize) {
        mTextSize = textSize;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }


}
