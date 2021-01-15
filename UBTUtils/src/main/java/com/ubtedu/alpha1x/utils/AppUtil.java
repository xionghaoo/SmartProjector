package com.ubtedu.alpha1x.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.LocaleList;
import android.provider.Settings;

import java.util.List;
import java.util.Locale;

/**
 * Created by qinicy on 2017/6/6.
 */

public class AppUtil {

    private AppUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static String getDeviceUUID(Context context) {
        String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        String mac = GetMacAddress.getMac(context);
        String devID = "";
        // 使用Android设备厂商信息字符串长度拼接
        devID += androidID +
                mac +
                Build.BOARD.length() +
                Build.BRAND.length() +
                Build.DEVICE.length() +
                Build.DISPLAY.length() +
                Build.HOST.length() +
                Build.ID.length() +
                Build.MANUFACTURER.length() +
                Build.MODEL.length() +
                Build.PRODUCT.length() +
                Build.TAGS.length() +
                Build.TYPE.length() +
                Build.SERIAL +
                Build.FINGERPRINT +
                Build.BOOTLOADER;

        return MD5Util.encodeByMD5(devID);
    }


    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "UbtEduAlpha";
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static String getUbtVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getApplicationMetaData(Context context, String key) {
        ApplicationInfo appInfo = null;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            return appInfo.metaData.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.e(e.getMessage());
        } catch (ClassCastException e) {
            LogUtil.e(e.getMessage());
        }
        return "";
    }
    /**
     * 该方法内部默认获取到的是系统语言
     * @param context
     * @return
     */
    @Deprecated
    public static boolean isZH(Context context) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        String language = locale.getLanguage();
        return language.contains("zh");
    }
    public static boolean isZH(Locale locale) {
        if (locale == null){
            return false;
        }
        String language = locale.getLanguage();
        return language.contains("zh");
    }
    /**
     * 该方法内部默认获取到的是系统语言
     * @param context
     * @return
     */
    @Deprecated
    public static boolean isEN(Context context) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        String language = locale.getLanguage();
        return language.contains("en");
    }
    public static boolean isEN(Locale locale) {
        if (locale == null){
            return false;
        }
        String language = locale.getLanguage();
        return language.contains("en");
    }

    public static boolean isThai(Locale locale) {
        if (locale == null){
            return false;
        }
        String language = locale.getLanguage();
        return language.contains("th");
    }

    public static Locale getAppLanguage(Context context) {
        Locale locale;
        Configuration configuration = context.getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = configuration.getLocales().get(0);
        } else {
            locale = configuration.locale;
        }
        return locale;
    }
    /**
     * 该方法内部默认获取到的是系统语言
     * @param context
     * @return
     */
    public static Locale getSystemLanguage() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }

        return locale;
    }

    public static boolean isSimpleChinese(Locale locale) {
        if (locale == null) {
            return false;
        }
        LogUtil.d(locale.toString());
        //是否中国大陆
        String country = locale.getCountry();
        boolean isCN = false;
        if (country != null) {
            isCN = country.equalsIgnoreCase("cn");
        }
        //是否简体中文
        boolean isHans = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (locale.getScript() != null && locale.getScript().toLowerCase().contains("hans")) {
                isHans = true;
            }
        }
        String language = locale.getLanguage();
        return language.toLowerCase().contains("zh") && (isCN || isHans);
    }

    /**
     * 该方法内部默认获取到的是系统语言
     * @param context
     * @return
     */
    @Deprecated
    public static boolean isSimpleChinese(Context context) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }


        LogUtil.d("system language:" + locale.toString());
        //是否中国大陆
        String country = locale.getCountry();
        boolean isCN = false;
        if (country != null) {
            isCN = country.equalsIgnoreCase("cn");
        }
        //是否简体中文
        boolean isHans = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (locale.getScript() != null && locale.getScript().toLowerCase().contains("hans")) {
                isHans = true;
            }
        }
        String language = locale.getLanguage();
        return language.toLowerCase().contains("zh") && (isCN || isHans);
    }

    public static boolean isActivityRunning(Context context, Class<? extends Activity> cls) {
        Intent intent = new Intent(context, cls);
        ComponentName cmpName = intent.resolveActivity(context.getPackageManager());
        boolean flag = false;
        if (cmpName != null) { // 说明系统中存在这个activity
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfoList = am.getRunningTasks(20);
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                if (taskInfo.baseActivity.equals(cmpName)) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }
}
