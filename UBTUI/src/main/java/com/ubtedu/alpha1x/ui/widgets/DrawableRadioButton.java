package com.ubtedu.alpha1x.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatRadioButton;

import com.ubtedu.base.ui.R;

/**
 * Created by qinicy on 2017/6/23.
 */

public class DrawableRadioButton extends AppCompatRadioButton {
    private Drawable mDrawableLeft;
    private Drawable mDrawableRight;
    private Drawable mDrawableTop;
    private int leftWidth;
    private int rightWidth;
    private int topWidth;
    private int leftHeight;
    private int rightHeight;
    private int topHeight;
    private Context mContext;
    private OnCheckedChangeListener mCheckedChangeListener;

    private boolean isNeedUpdateCompoundDrawables;
    public DrawableRadioButton(Context context) {
        this(context, null, 0);
    }

    public DrawableRadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawableRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DrawableRadioButton);
        mDrawableLeft = typedArray.getDrawable(R.styleable.DrawableRadioButton_leftDrawable);
        mDrawableRight = typedArray.getDrawable(R.styleable.DrawableRadioButton_rightDrawable);
        mDrawableTop = typedArray.getDrawable(R.styleable.DrawableRadioButton_topDrawable);
        if (mDrawableLeft != null) {
            leftWidth = typedArray.getDimensionPixelOffset(R.styleable.DrawableRadioButton_leftDrawableWidth, dip2px(context, 20));
            leftHeight = typedArray.getDimensionPixelOffset(R.styleable.DrawableRadioButton_leftDrawableHeight, dip2px(context, 20));
        }
        if (mDrawableRight != null) {
            rightWidth = typedArray.getDimensionPixelOffset(R.styleable.DrawableRadioButton_rightDrawableWidth, dip2px(context, 20));
            rightHeight = typedArray.getDimensionPixelOffset(R.styleable.DrawableRadioButton_rightDrawableHeight, dip2px(context, 20));
        }
        if (mDrawableTop != null) {
            topWidth = typedArray.getDimensionPixelOffset(R.styleable.DrawableRadioButton_topDrawableWidth, dip2px(context, 20));
            topHeight = typedArray.getDimensionPixelOffset(R.styleable.DrawableRadioButton_topDrawableHeight, dip2px(context, 20));
        }

        typedArray.recycle();
        isNeedUpdateCompoundDrawables = true;
    }


    private void updateDrawableState(boolean isChecked) {

        int[] states;
        if (isChecked) {
            states = new int[]{android.R.attr.state_checked};
        } else {
            states = new int[]{-android.R.attr.state_checked};
        }

        if (mDrawableLeft != null) {
            mDrawableLeft.setState(states);
        }
        if (mDrawableTop != null) {
            mDrawableTop.setState(states);
        }
        if (mDrawableRight != null) {
            mDrawableRight.setState(states);
        }
        invalidate();
    }


    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mDrawableLeft != null) {
            mDrawableLeft.setBounds(0, 0, leftWidth, leftHeight);
        }
        if (mDrawableRight != null) {
            mDrawableRight.setBounds(0, 0, rightWidth, rightHeight);
        }
        if (mDrawableTop != null) {
            mDrawableTop.setBounds(0, 0, topWidth, topHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isNeedUpdateCompoundDrawables){
            updateCompoundDrawables();
            isNeedUpdateCompoundDrawables = false;
        }
        super.onDraw(canvas);
    }


    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        updateDrawableState(checked);
    }

    /**
     * 设置左侧图片并重绘
     */
    public void setDrawableLeft(Drawable drawableLeft) {
        this.mDrawableLeft = drawableLeft;
        isNeedUpdateCompoundDrawables = true;
        invalidate();
    }

    /**
     * 设置左侧图片并重绘
     */
    public void setDrawableLeft(int drawableLeftRes) {
        this.mDrawableLeft = mContext.getResources().getDrawable(drawableLeftRes);
        isNeedUpdateCompoundDrawables = true;
        invalidate();
    }

    /**
     * 设置右侧图片并重绘
     */
    public void setDrawableRight(Drawable drawableRight) {
        this.mDrawableRight = mDrawableLeft;
        isNeedUpdateCompoundDrawables = true;
        invalidate();
    }

    /**
     * 设置右侧图片并重绘
     */
    public void setDrawableRight(int drawableRightRes) {
        this.mDrawableRight = mContext.getResources().getDrawable(drawableRightRes);
        isNeedUpdateCompoundDrawables = true;
        invalidate();
    }

    /**
     * 设置上部图片并重绘
     */
    public void setDrawable(Drawable drawableTop) {
        this.mDrawableTop = drawableTop;
        isNeedUpdateCompoundDrawables = true;
        invalidate();
    }

    /**
     * 设置右侧图片并重绘
     */
    public void setDrawableTop(int drawableTopRes) {
        this.mDrawableTop = mContext.getResources().getDrawable(drawableTopRes);
        isNeedUpdateCompoundDrawables = true;
        invalidate();
    }

    private void updateCompoundDrawables(){
        this.setCompoundDrawables(mDrawableLeft, mDrawableTop, mDrawableRight, null);
    }
}
