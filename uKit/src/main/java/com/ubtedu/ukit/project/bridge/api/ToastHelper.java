/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.bridge.api;

import com.ubtedu.ukit.common.base.UKitUIDelegate;

import java.lang.ref.WeakReference;

/**
 * @Author naOKi
 * @Date 2019/5/08
 **/
public class ToastHelper {

    private static WeakReference<UKitUIDelegate> sShowToastActivityWef;
    private static WeakReference<UKitUIDelegate> sShowToastDialogWef;

    public static void updateActivityUIDelegate(UKitUIDelegate uiDelegate) {
        sShowToastActivityWef = new WeakReference<>(uiDelegate);
        if(sShowToastDialogWef != null) {
            sShowToastDialogWef.clear();
        }
    }

    public static void cleanActivityUIDelegate(UKitUIDelegate uiDelegate) {
        if(sShowToastActivityWef == null || sShowToastActivityWef.get() != uiDelegate) {
            return;
        }
        sShowToastActivityWef.clear();
    }

    public static void updateDialogUIDelegate(UKitUIDelegate uiDelegate, boolean isVisible) {
        if(isVisible) {
            updateDialogUIDelegate(uiDelegate);
        } else {
            cleanDialogUIDelegate(uiDelegate);
        }
    }

    private static void updateDialogUIDelegate(UKitUIDelegate uiDelegate) {
        sShowToastDialogWef = new WeakReference<>(uiDelegate);
    }

    private static void cleanDialogUIDelegate(UKitUIDelegate uiDelegate) {
        if(sShowToastDialogWef == null || sShowToastDialogWef.get() != uiDelegate) {
            return;
        }
        sShowToastDialogWef.clear();
    }

    private static UKitUIDelegate getUIDelegate() {
        if(sShowToastDialogWef != null && sShowToastDialogWef.get() != null) {
            return sShowToastDialogWef.get();
        }
        if(sShowToastActivityWef != null && sShowToastActivityWef.get() != null) {
            return sShowToastActivityWef.get();
        }
        return null;
    }

    public static void toastShort(String msg) {
        UKitUIDelegate uiDelegate = getUIDelegate();
        if(uiDelegate == null) {
            return;
        }
        uiDelegate.toastShort(msg);
    }

    public static void toastLong(String msg) {
        UKitUIDelegate uiDelegate = getUIDelegate();
        if(uiDelegate == null) {
            return;
        }
        uiDelegate.toastLong(msg);
    }

}
