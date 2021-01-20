package com.ubtedu.ukit.common.view.badge;

import android.content.Context;
/**
 * @Author qinicy
 * @Date 2019/6/5
 **/
public class DisplayUtil {
    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}