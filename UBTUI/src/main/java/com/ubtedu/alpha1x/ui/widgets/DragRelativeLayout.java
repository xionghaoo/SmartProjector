package com.ubtedu.alpha1x.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.customview.widget.ViewDragHelper;

/**
 * Created by qinicy on 2017/6/8.
 */

public class DragRelativeLayout extends RelativeLayout {
    private ViewDragHelper mDragger;

    public DragRelativeLayout(Context context) {
        super(context);
        initDragger();
    }

    public DragRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDragger();
    }

    public DragRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDragger();
    }

    private void initDragger() {
        mDragger = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return true;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                final int leftBound = getPaddingLeft() - child.getWidth()/4;
                final int rightBound = getWidth() - child.getWidth() - leftBound;
                final int newLeft = Math.min(Math.max(left, leftBound), rightBound);
                return newLeft;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                final int topBound = getPaddingTop() -child.getHeight()/4;
                final int bottomBound = getHeight() - child.getHeight() - topBound;
                final int newTop = Math.min(Math.max(top, topBound), bottomBound);
                return newTop;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        return mDragger.shouldInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        if (pointerCount == 1) {

            try {
                mDragger.processTouchEvent(event);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }


        }
        return true;
    }
}
