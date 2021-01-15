package com.ubtedu.deviceconnect.libs.utils;

/**
 * @Author naOKi
 * @Date 2019/02/20
 **/
public class URoNumberConversionUtil {

    private URoNumberConversionUtil() {}

    public static int hex2Integer(String s, int radix) {
        return hex2Integer(s, radix, 0);
    }

    public static int hex2Integer(String s, int radix, int defaultValue) {
        try {
            return (int)Long.parseLong(s, radix);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static int hex2IntegerE(String s, int radix) {
        try {
            return (int)Long.parseLong(s, radix);
        } catch (NumberFormatException e) {
            throw e;
        }
    }

}
