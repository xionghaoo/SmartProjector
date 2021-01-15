/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.bridge.api;


import com.ubtedu.ukit.application.BasicEventDelegate;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.common.base.UKitBaseActivity;
import com.ubtedu.ukit.common.base.UKitUIDelegate;

import java.lang.ref.WeakReference;


/**
 * @Author qinicy
 * @Date 2019/4/26
 **/
public class ActivityHelper {

    private static WeakReference<UKitUIDelegate> sShowLoadingWef;

    public static UKitBaseActivity getResumeActivity() {
        if (UKitApplication.getInstance().getEventDelegate() instanceof BasicEventDelegate) {
            BasicEventDelegate delegate = (BasicEventDelegate) UKitApplication.getInstance().getEventDelegate();
            return delegate.getResumeActivity();
        }
        return null;
    }

    public static void showLoading(boolean cancelAble) {
        showLoading(cancelAble, null);
    }

    public static void showLoading(boolean cancelAble, String message) {
        showLoading(cancelAble, message, -1);
    }

    public static void showLoading(boolean cancelAble, String message, int lottieAnimationRawId) {
        if (sShowLoadingWef != null && sShowLoadingWef.get() != null) {
            sShowLoadingWef.get().hideLoading();
            sShowLoadingWef = null;
        }
        UKitBaseActivity activity = getResumeActivity();
        if ( activity!= null) {
            UKitUIDelegate delegate = activity.getUIDelegate();
            if (delegate != null) {
                delegate.showLoading(cancelAble, message, lottieAnimationRawId);
                sShowLoadingWef = new WeakReference<>(delegate);
            }

        }
    }

    public static void hideLoading() {
        if (sShowLoadingWef != null && sShowLoadingWef.get() != null) {
            sShowLoadingWef.get().hideLoading();
            sShowLoadingWef = null;
        }
    }
}
