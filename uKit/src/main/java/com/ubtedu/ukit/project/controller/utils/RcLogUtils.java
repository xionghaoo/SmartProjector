package com.ubtedu.ukit.project.controller.utils;

import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.ukit.BuildConfig;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @Author naOKi
 **/
public class RcLogUtils {

    private static final String TAG = "RemoteController";

    private RcLogUtils() {}

    public static void d(String msg) {
        LogUtil.d(TAG, msg);
    }

    public static void d(String fmt, Object... params) {
        LogUtil.d(TAG, String.format(fmt, params));
    }

    public static void w(String msg) {
        LogUtil.w(TAG, msg);
    }

    public static void w(String fmt, Object... params) {
        LogUtil.w(TAG, String.format(fmt, params));
    }

    public static void e(String msg) {
        if(BuildConfig.DEBUG) {
            LogUtil.e(TAG, msg);
        }
    }

    public static void e(String fmt, Object... params) {
        if(BuildConfig.DEBUG) {
            LogUtil.e(TAG, String.format(fmt, params));
        }
    }

    public static void e(Throwable e) {
        if(BuildConfig.DEBUG) {
            LogUtil.e(TAG, toErrorString(e));
        }
    }

    public static void e(String msg, Throwable e) {
        if(BuildConfig.DEBUG) {
            LogUtil.e(TAG, msg);
            LogUtil.e(TAG, toErrorString(e));
        }
    }

    private static String toErrorString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

}
