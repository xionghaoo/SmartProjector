package com.ubtedu.alpha1x.utils;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by qinicy on 2017/6/7.
 */

public class AnimUtil {
    /**
     * 改变view的宽的动画
     *
     * @param view
     * @param from
     * @param to
     * @param duration
     */
    public static void scaleWidth(final View view, float from, float to, int duration) {
        scaleWH(view,true,from,to,duration);
    }
    /**
     * 改变view的height的动画
     *
     * @param view
     * @param from 比例，不是真实值
     * @param to 比例，不是真实值
     * @param duration
     */
    public static void scaleHeight(final View view, float from, float to, int duration) {
        scaleWH(view,false,from,to,duration);
    }
    private static void scaleWH(final View view, final boolean isWidth, float from, float to, int duration) {
        if (view == null) {
            return;
        }
        final ViewGroup.LayoutParams lp = view.getLayoutParams();
        
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(from, to);
        valueAnimator.setDuration(duration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (float) animation.getAnimatedValue();
                if (isWidth) {
                    lp.width *= x;
                    view.setLayoutParams(lp);
                } else {
                    lp.height *= x;
                }
                view.setLayoutParams(lp);

            }
        });
        valueAnimator.start();
    }
}
