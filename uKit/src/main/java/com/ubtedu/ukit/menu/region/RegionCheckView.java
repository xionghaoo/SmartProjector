/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.menu.region;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubtedu.ukit.R;

/**
 * @Author qinicy
 * @Date 2018/12/12
 **/
public class RegionCheckView extends FrameLayout implements Checkable {
    private RegionInfo mItemInfo;
    private View mRegionView;
    private TextView mRegionTv;
    private View mCheckLyt;
    private boolean isCheck;
    private String mLanguageName;
    private int mRegionResId;

    public RegionCheckView(Context context) {
        super(context);
        initView(context);
    }

    public RegionCheckView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public RegionCheckView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public RegionCheckView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_region_check, this);
        mRegionView = findViewById(R.id.language_region_view);
        if (mRegionResId > 0) {
            mRegionView.setBackgroundResource(mRegionResId);
        }
        mRegionTv = findViewById(R.id.language_region_tv);
        if (!TextUtils.isEmpty(mLanguageName)) {
            mRegionTv.setText(mLanguageName);
        }
        mCheckLyt = findViewById(R.id.language_region_check_lyt);

    }

    @Override
    public void setChecked(boolean checked) {
        isCheck = checked;
        updateCheckUI();
    }

    @Override
    public boolean isChecked() {
        return isCheck;
    }

    @Override
    public void toggle() {
        isCheck = !isCheck;
        updateCheckUI();
    }

    public RegionInfo getItemInfo() {
        return mItemInfo;
    }

    public void setItemInfo(RegionInfo itemInfo) {
        mItemInfo = itemInfo;
        if (mItemInfo != null){
            setRegionImage(itemInfo.backgroundResourceId);
            setLanguageName(itemInfo.displayName);

        }
    }

    public void setRegionImage(@DrawableRes int id) {
        mRegionResId = id;
        if (mRegionView != null) {
            mRegionView.setBackgroundResource(id);
        }
    }

    public void setLanguageName(String lang) {
        mLanguageName = lang;
        if (mRegionTv != null && !TextUtils.isEmpty(lang)) {
            mRegionTv.setText(lang);
        }
    }

    private void updateCheckUI() {
        mCheckLyt.setVisibility(isCheck ? VISIBLE : GONE);
        if (isCheck) {
            mRegionTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            mRegionTv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
    }
}
