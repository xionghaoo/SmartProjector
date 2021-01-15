/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.menu.settings;


import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.ubtedu.alpha1x.utils.GsonUtil;
import com.ubtedu.alpha1x.utils.SharedPreferenceUtils;
import com.ubtedu.ukit.BuildConfig;
import com.ubtedu.ukit.application.ServerEnv;
import com.ubtedu.ukit.common.flavor.Channel;
import com.ubtedu.ukit.common.flavor.Flavor;
import com.ubtedu.ukit.menu.MenuConstants;
import com.ubtedu.ukit.menu.region.RegionFactory;
import com.ubtedu.ukit.menu.region.RegionHelper;
import com.ubtedu.ukit.menu.region.RegionInfo;

import java.util.Locale;

import static com.ubtedu.ukit.application.ServerConfig.SP_SERVER_CONFIG;

/**
 * @Author qinicy
 * @Date 2018/12/13
 **/
public class Settings {

    private final static String SP_APP_FIRST_LAUNCH = "app_first_launch";
    private static SharedPreferences sPreferences;
    private static Context sContext;
    @TargetDevice.Values
    private static int sTargetDevice;

    public static void init(Context context) {
        sContext = context;
        sPreferences = context.getSharedPreferences(SharedPreferenceUtils.PREF_NAME, Context.MODE_PRIVATE);
        boolean isFirstLaunch = sPreferences.getBoolean(SP_APP_FIRST_LAUNCH, true);
        //充电保护默认打开,语言第一次跟随系统
        if (isFirstLaunch) {
            setChargingProtectionState(true);
            initRegion();
            sPreferences.edit().putBoolean(SP_APP_FIRST_LAUNCH, false).apply();
        }
    }

    private static void initRegion() {
        RegionInfo region = getSystemRegion();
        setRegion(region);
    }

    public static RegionInfo getSystemRegion() {
        RegionFactory factory = new RegionFactory(sContext);
        RegionInfo region = factory.createGL();

        if (RegionHelper.isChinaMainland()) {
            region = factory.createCN();
        }
        if (RegionHelper.isNA()) {
            region = factory.createNA();
        }
        if (RegionHelper.isPL()) {
            region = factory.createPL();
        }
        // FIXME: 屏蔽越南语 duanxinning at 2019/12/4 此处如不屏蔽，当系统语言为越南语时，app内文字会显示成越南语
        if (RegionHelper.isVN() && getServerEnvConfig() != ServerEnv.RELEASE) {
            region = factory.createVN();
        }
        if (RegionHelper.isKR() && getServerEnvConfig() != ServerEnv.RELEASE) {
            region = factory.createKR();
        }
        if (RegionHelper.isBD()&&getServerEnvConfig() != ServerEnv.RELEASE) {
            region = factory.createBD();
        }
        return region;
    }


    public static Locale getLocale() {
        RegionInfo region = getRegion();
        if (region != null && region.locale != null) {
            return region.locale;
        }
        RegionFactory factory = new RegionFactory(sContext);
        return factory.createGL().locale;
    }


    public static void setRegion(RegionInfo region) {
        if (region != null) {
            sPreferences.edit().putString(MenuConstants.SP_KEY_REGION, GsonUtil.get().toJson(region)).apply();
        }
    }

    @NonNull
    public static RegionInfo getRegion() {
        String json = sPreferences.getString(MenuConstants.SP_KEY_REGION, "");
        RegionInfo region = GsonUtil.get().toObject(json, RegionInfo.class);
        if (region == null) {
            region = new RegionFactory(sContext).createCN();
        }
        return region;
    }

    public static void setChargingProtectionState(boolean isChecked) {
        sPreferences.edit().putBoolean(MenuConstants.SP_KEY_CHARGING_PROTECTION, isChecked).apply();
    }

    public static boolean isChargingProtection() {
        return sPreferences.getBoolean(MenuConstants.SP_KEY_CHARGING_PROTECTION, false);
    }

    public static String getUnityTag() {
        RegionInfo region = getRegion();
        if (region != null) {
            return region.unityTag;
        }
        return RegionInfo.TAG_CN;
    }


    public static String getUnityLanguage() {
        RegionInfo region = getRegion();
        if (region != null) {
            return region.unityLanguage;
        }
        return RegionInfo.TAG_CN;
    }


    public static boolean isRegionCN() {
        return RegionInfo.REGION_CN.equals(getRegion().name);
    }

    // FIXME: 只在getSystemRegion()中调用，初始化语言时如果调用ServerConfig会导致栈溢出，后续开放韩语删除此方法 duanxinning at 2019/8/16
    private static ServerEnv getServerEnvConfig() {
        String defaultName = BuildConfig.DEBUG ? ServerEnv.TEST.name() : ServerEnv.RELEASE.name();
        String envName = sPreferences.getString(SP_SERVER_CONFIG, defaultName);
        try {
            return ServerEnv.valueOf(envName);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return ServerEnv.RELEASE;
    }

    public static void setTargetDevice(@TargetDevice.Values int device) {
        if (TargetDevice.NONE == device || TargetDevice.UKIT1 == device || TargetDevice.UKIT2 == device) {
            sTargetDevice = device;
            sPreferences.edit().putInt(MenuConstants.SP_KEY_DEVICE_SELECTED, device).apply();
        }
    }

    /**
     * 获取目标主控，ukit1.0 {@link TargetDevice#UKIT1}或ukit2.0 {@link TargetDevice#UKIT2}，
     * 什么都没选过的话，返回{@link TargetDevice#NONE}
     */
    @TargetDevice.Values
    public static int getTargetDevice() {
        if (Flavor.getChannel().getId() == Channel.NA.getId()){
            return TargetDevice.UKIT1;
        }
        if (sTargetDevice == TargetDevice.NONE) {
            sTargetDevice = sPreferences.getInt(MenuConstants.SP_KEY_DEVICE_SELECTED, TargetDevice.NONE);
        }
        return sTargetDevice;
    }

    public static boolean isTargetDevice(@TargetDevice.Values int device) {
        return device == getTargetDevice();
    }
}
