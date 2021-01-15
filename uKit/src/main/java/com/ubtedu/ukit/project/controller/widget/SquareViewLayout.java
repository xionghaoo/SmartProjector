package com.ubtedu.ukit.project.controller.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @Author naOKi
 * @Date 2018/12/05
 **/
public class SquareViewLayout extends FrameLayout {
    public static final int TRUE = -1;

    public static final int HEIGHT_AS_WIDTH                  = 0;
    public static final int WIDTH_AS_HEIGHT                  = 1;
    public static final int VERB_COUNT                       = 2;

    private int[] _rule = new int[VERB_COUNT];

    public SquareViewLayout(@NonNull Context context) {
        super(context);
    }

    public SquareViewLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        if(measuredWidth == measuredHeight) {
            return;
        }
        if(isHeightAsWidth()) {
            int realSpec = MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY);
            super.onMeasure(realSpec, realSpec);
        }
        if(isWidthAsHeight()) {
            int realSpec = MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY);
            super.onMeasure(realSpec, realSpec);
        }
    }

    private void addRule(int verb, int value) {
        if(verb < 0 || verb >= VERB_COUNT) {
            return;
        }
        _rule[verb] = value;
    }

    public void setHeightAsWidth() {
        addRule(HEIGHT_AS_WIDTH, TRUE);
        addRule(WIDTH_AS_HEIGHT, 0);
    }

    public void setWidthAsHeight() {
        addRule(WIDTH_AS_HEIGHT, TRUE);
        addRule(HEIGHT_AS_WIDTH, 0);
    }

    public boolean isHeightAsWidth() {
        return _rule[HEIGHT_AS_WIDTH] == TRUE;
    }

    public boolean isWidthAsHeight() {
        return _rule[WIDTH_AS_HEIGHT] == TRUE;
    }

}
