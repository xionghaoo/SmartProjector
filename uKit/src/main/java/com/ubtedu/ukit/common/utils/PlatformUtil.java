package com.ubtedu.ukit.common.utils;

import android.content.pm.PackageManager;

import com.ubtedu.ukit.application.UKitApplication;

/**
 * @Author naOKi
 * @Date 2019/05/06
 **/
public class PlatformUtil {

    public static final String PLATFORM_ANDROID = "android";
    public static final String PLATFORM_CHROMEBOOK = "chromebook";

    private static final boolean isChromebookDevice;
    private static final boolean hasAccelerometerSensor;

    static {
        PackageManager pm = UKitApplication.getInstance().getPackageManager();
        isChromebookDevice = pm.hasSystemFeature("org.chromium.arc.device_management");
        hasAccelerometerSensor = pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
    }

    private PlatformUtil() {}

    public static boolean isChromebookDevice() {
        return isChromebookDevice;
    }

    public static boolean hasAccelerometerSensor() {
        return hasAccelerometerSensor;
    }

    public static String getDevicePlatform() {
        return isChromebookDevice() ? PLATFORM_CHROMEBOOK : PLATFORM_ANDROID;
    }

}
