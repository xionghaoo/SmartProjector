/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.home.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RawRes;

import com.airbnb.lottie.LottieAnimationView;
import com.ubtedu.ukit.R;

/**
 * @Author qinicy
 * @Date 2018/11/8
 **/
public class MainTabItemView extends LinearLayout {
    private String mTabName;
    private int mTabAnimResId;
    private TextView mNameTv;
    private View mIndicatorView;
    private boolean isCheck;
    private int mCheckTextColor = Color.parseColor("#171E2B");
    private int mTextColor = Color.parseColor("#555B67");
    private LottieAnimationView mLottieAnimationView;
    private int mPosition;

    public MainTabItemView(Context context) {
        super(context);
        initView(context, null, 0);
    }

    public MainTabItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public MainTabItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    public void setLottieAnimation(@RawRes int id) {
        if (mLottieAnimationView != null) {
            mLottieAnimationView.setAnimation(id);
        }
    }

    private void initView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.MainTabItemView,
                defStyleAttr, 0);
        try {
            mTabAnimResId = a.getResourceId(R.styleable.MainTabItemView_tabAnimRaw, 0);
            mTabName = a.getString(R.styleable.MainTabItemView_tabItemText);
            isCheck = a.getBoolean(R.styleable.MainTabItemView_tabItemCheck, false);
        } finally {
            a.recycle();
        }
        LayoutInflater.from(context).inflate(R.layout.view_main_tab_item, this);

        mLottieAnimationView = findViewById(R.id.lottie_view);

        mNameTv = findViewById(R.id.tab_name_tv);
        mIndicatorView = findViewById(R.id.tab_indicator_view);

//        mLottieAnimationView.setAnimation(mTabAnimResId);
        mNameTv.setText(mTabName);
        updateCheckStateUI();
    }

    public void playAnimation() {
        mLottieAnimationView.playAnimation();
    }

    @Override
    public void clearAnimation() {
        super.clearAnimation();
        if (mLottieAnimationView.isAnimating()) {
            mLottieAnimationView.setProgress(1);
            mLottieAnimationView.cancelAnimation();
        }
    }

    public void setCheck(boolean check) {
        isCheck = check;
        updateCheckStateUI();
    }

    public boolean isChecked() {
        return isCheck;
    }

    private void updateCheckStateUI() {
        if (isCheck) {
            mIndicatorView.setVisibility(VISIBLE);
            mNameTv.setTextColor(mCheckTextColor);
        } else {
            mIndicatorView.setVisibility(INVISIBLE);
            mNameTv.setTextColor(mTextColor);
        }
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public void setTabName(String name) {
        mNameTv.setText(name);
    }
}
