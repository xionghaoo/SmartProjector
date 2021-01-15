package com.ubtedu.deviceconnect.libs.utils;

import android.text.TextUtils;
import android.util.Log;

import com.ubtedu.deviceconnect.libs.BuildConfig;
import com.ubtedu.deviceconnect.libs.base.URoSDK;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

/**
 * @Author naOKi
 * @Date 2019/06/28
 **/
public class URoLogUtils {

    private static final String TAG = "URoSDK";

    private static URoLogInfoDelegate logDelegate = null;

    private URoLogUtils() {}

    public static void setLogDelegate(URoLogInfoDelegate delegate) {
        logDelegate = delegate;
    }

    private static void printLogDelegate(int level, String tag, String content) {
        if(logDelegate != null) {
            String runtimeTag = getRuntimeTag();
            tag = TextUtils.isEmpty(runtimeTag) ? tag : runtimeTag;
            logDelegate.onLogPrint(level, tag, content);
        }
    }

    private static String getRuntimeTag() {
        String tag = TAG;
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[6];
        String className = stackTraceElement.getClassName();
        if (className != null) {
            String[] classPath = className.split("\\.");
            if(classPath.length > 0) {
                tag = classPath[classPath.length - 1];
            }
        }
        return tag;
    }

    public static void d(String msg) {
        if(BuildConfig.DEBUG || URoSDK.getInstance().isDebug()) {
            Log.d(TAG, msg);
        }
        printLogDelegate(Log.DEBUG, TAG, msg);
    }

    public static void d(String fmt, Object... params) {
        d(String.format(Locale.US, fmt, params));
    }

    public static void w(String msg) {
        if(BuildConfig.DEBUG || URoSDK.getInstance().isDebug()) {
            Log.w(TAG, msg);
        }
        printLogDelegate(Log.WARN, TAG, msg);
    }

    public static void w(String fmt, Object... params) {
        w(String.format(Locale.US, fmt, params));
    }

    public static void i(String msg) {
        if(BuildConfig.DEBUG || URoSDK.getInstance().isDebug()) {
            Log.i(TAG, msg);
        }
        printLogDelegate(Log.INFO, TAG, msg);
    }

    public static void i(String fmt, Object... params) {
        i(String.format(Locale.US, fmt, params));
    }

    public static void e(String msg) {
        if(BuildConfig.DEBUG || URoSDK.getInstance().isDebug()) {
            Log.e(TAG, msg);
        }
        printLogDelegate(Log.ERROR, TAG, msg);
    }

    public static void e(String fmt, Object... params) {
        e(String.format(Locale.US, fmt, params));
    }

    public static void e(Throwable e) {
        e(toErrorString(e));
    }

    public static void e(String msg, Throwable e) {
        e(msg);
        e(toErrorString(e));
    }

    private static String toErrorString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

}
