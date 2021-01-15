/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.utils;

import com.ubtedu.ukit.BuildConfig;

/**
 * @Author qinicy
 * @Date 2018/11/6
 **/
public class VersionUtil {
    private static String VERSION_NAME;
    private static final String ANDROID_PLATFORM_VERSION_FLAG = ".10";

    /**
     * v1.0.3.3-g6c66a66 => 1.0.3.10
     *Just ror yanshee
     * @deprecated
     * @return
     */
    public static String generateServerVersionName() {
        if (VERSION_NAME == null) {
            String src = BuildConfig.VERSION_NAME;
            VERSION_NAME = src.substring(1, src.lastIndexOf(".")) + ANDROID_PLATFORM_VERSION_FLAG;
        }
        return VERSION_NAME;
    }
    public static String generateOtaVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    public static String generateAppStoreVersionName() {
        String src = BuildConfig.VERSION_NAME;
        String v = src.substring(0, src.lastIndexOf("."));

        String[] ss = src.split("\\.");
        if (ss.length >= 3){
            v = ss[0]+"."+ss[1]+"."+ss[2];
        }
        return v;
    }

    public static String generateDevelopVersionName() {

        return BuildConfig.VERSION_NAME;
    }


    public static int getVersionCode() {
        return BuildConfig.VERSION_CODE;
    }

    /**
     * v1.0.3.3-g6c66a66 => 1.0.3
     *
     * @return
     */
    public static String getSophixVersion() {
        String src = BuildConfig.VERSION_NAME;
        String version = src.substring(1, src.lastIndexOf("."));
//        String version = src.substring(1, src.lastIndexOf("-"));
        return version;
    }
}
