package com.ubtedu.alpha1x.utils;

import android.util.Log;

/**
 * Created by qinicy on 3016/5/3.
 * log controller
 */
public class LogUtil {

    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NOTHING = 6;

    public static int LEVEL = VERBOSE;

    public static void v(String msg) {

        if (LEVEL <= VERBOSE) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = generateTAG(stackTraceElement);
            String info = generateLogInfo(stackTraceElement);
            Log.v(tag, info + msg);
        }
    }

    public static void d(String msg) {

        if (LEVEL <= DEBUG) {
            StackTraceElement[] es = Thread.currentThread().getStackTrace();
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = generateTAG(stackTraceElement);
            String info = generateLogInfo(stackTraceElement);
            Log.d(tag, info + msg);
        }
    }

    public static void i(String msg) {

        if (LEVEL <= INFO) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = generateTAG(stackTraceElement);
            String info = generateLogInfo(stackTraceElement);
            Log.i(tag, info + msg);
        }
    }

    public static void w(String msg) {
        if (LEVEL <= WARN) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = generateTAG(stackTraceElement);
            String info = generateLogInfo(stackTraceElement);
            Log.w(tag, info + msg);
        }
    }

    public static void e(String msg) {
        if (LEVEL <= ERROR) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = generateTAG(stackTraceElement);
            String info = generateLogInfo(stackTraceElement);
            Log.e(tag, info + msg);
        }
    }

    public static void v(String tag, String msg) {
        if (LEVEL <= VERBOSE) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (LEVEL <= DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (LEVEL <= INFO) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (LEVEL <= WARN) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (LEVEL <= ERROR) {
            Log.e(tag, msg);
        }
    }

    private static String generateTAG(StackTraceElement stackTraceElement) {
        if (stackTraceElement == null)
            return "UBT";
        String className = stackTraceElement.getClassName();
        if (className != null) {
            int index = className.lastIndexOf(".");
            if (className.length() - 1 >= index + 1) {
                return className.substring(index + 1);
            } else {
                return "UBT";
            }
        }
        return "UBT";
    }

    private static String generateLogInfo(StackTraceElement stackTraceElement) {
        if (stackTraceElement == null)
            return "";
        int line = stackTraceElement.getLineNumber();
        String method = stackTraceElement.getMethodName();
        return "[" + method + "]" + "[" + line + "]:";
    }
}
