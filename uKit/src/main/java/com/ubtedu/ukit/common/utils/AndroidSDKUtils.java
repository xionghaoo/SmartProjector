package com.ubtedu.ukit.common.utils;

import android.os.Build;

/**
 * @Author qinicy
 * @Date 2019/5/8
 **/
public class AndroidSDKUtils {
    public static boolean isAtLeastQ() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.P;
    }
}
