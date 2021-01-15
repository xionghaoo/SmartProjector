/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.locale;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.ubtedu.alpha1x.ui.layoutmanager.ScaleLayoutManager;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.ukit.R;

/**
 * @Author qinicy
 * @Date 2019/2/21
 **/
public class LocaleSelectLayoutManager extends ScaleLayoutManager {
    private static final int TEXT_COLOR_NORMAL = Color.parseColor("#929AA8");
    private static final int TEXT_COLOR_SELECTED = Color.parseColor("#68BCFF");

    public LocaleSelectLayoutManager(Context context, int itemSpace) {
        super(context, itemSpace, LinearLayoutManager.VERTICAL);
    }

    @Override
    protected void setItemViewProperty(View itemView, float targetOffset) {
        LogUtil.d("targetOffset:" + targetOffset + "  itemView" + itemView);
        if (itemView instanceof ViewGroup) {
            ViewGroup item = (ViewGroup) itemView;
            TextView nameView = item.findViewById(R.id.locale_name_tv);
            int selectColor = calculateColor(targetOffset);

            nameView.setTextColor(selectColor);
            TextView codeView = item.findViewById(R.id.locale_code_tv);
            codeView.setTextColor(selectColor);
            float scale = calculateScale(targetOffset + mSpaceMain);
            nameView.setScaleX(scale);
            nameView.setScaleY(scale);
            codeView.setScaleX(scale);
            codeView.setScaleY(scale);
            final float alpha = calAlpha(targetOffset);
            nameView.setAlpha(alpha);
            codeView.setAlpha(alpha);
        }

    }


    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        LogUtil.d("onScrollStateChanged state:"+state);
    }

    @Override
    public void scrollToPosition(int position) {
        super.scrollToPosition(position);
    }

    private boolean isSelected(float targetOffset) {
        return Math.abs(targetOffset) - mDecoratedMeasurement < 0;
    }

    private int calculateColor(float targetOffset) {
        int color = TEXT_COLOR_NORMAL;

        float deltaX = Math.abs(targetOffset);
        if (deltaX - mDecoratedMeasurement <= 0) {
            color = TEXT_COLOR_SELECTED;
        }
        return color;
    }
}
