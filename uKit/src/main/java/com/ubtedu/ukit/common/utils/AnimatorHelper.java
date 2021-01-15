package com.ubtedu.ukit.common.utils;

import android.animation.ValueAnimator;

import com.ubtedu.alpha1x.utils.LogUtil;

import java.lang.reflect.Method;

/**
 * @Author naOKi
 * @Date 2019/01/22
 **/
public class AnimatorHelper {

    private AnimatorHelper() {}

    public static void init() {
        setDefaultDurationScale(1.0f);
    }

    public static boolean setDefaultDurationScale(float durationScale) {
        try {
            Class cls = ValueAnimator.class;
            Method setDurationScaleMethod = cls.getDeclaredMethod("setDurationScale", float.class);
            setDurationScaleMethod.setAccessible(true);
            setDurationScaleMethod.invoke(null, durationScale);
            return true;
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
            return false;
        }
    }

    public static boolean setOverrideDurationScale(ValueAnimator animator, float durationScale) {
        try {
            Class cls = ValueAnimator.class;
            Method setDurationScaleMethod = cls.getDeclaredMethod("overrideDurationScale", float.class);
            setDurationScaleMethod.setAccessible(true);
            setDurationScaleMethod.invoke(animator, durationScale);
            return true;
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
            return false;
        }
    }

}
