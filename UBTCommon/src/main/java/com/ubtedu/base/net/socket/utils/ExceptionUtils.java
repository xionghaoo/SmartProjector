package com.ubtedu.base.net.socket.utils;

/**
 */
public class ExceptionUtils {
    private static final String TAG = "XAndroidSocket";

    public static void throwException(String message) {
        throw new IllegalStateException(TAG + " : " + message);
    }
}