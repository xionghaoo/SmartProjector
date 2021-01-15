package com.ubtedu.deviceconnect.libs.utils;

import java.io.Closeable;

/**
 * @Author naOKi
 * @Date 2019/06/28
 **/
public class URoIoUtils {

    private URoIoUtils() {}

    public static void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {
            // do nothing
        }
    }

}
