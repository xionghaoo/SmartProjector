/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.application;

import android.annotation.SuppressLint;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.ubtedu.alpha1x.core.base.application.UbtBaseApplication;
import com.ubtedu.alpha1x.core.base.delegate.EventDelegate;
import com.ubtedu.alpha1x.utils.AppUtil;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.alpha1x.utils.SharedPreferenceUtils;
import com.ubtedu.base.UBTCrashManager;
import com.ubtedu.base.net.manager.NetworkManager;
import com.ubtedu.deviceconnect.libs.base.URoSDK;
import com.ubtedu.ukit.bluetooth.ota.OtaHelper;
import com.ubtedu.ukit.common.exception.ExceptionHelper;
import com.ubtedu.ukit.common.files.FileHelper;
import com.ubtedu.ukit.common.cloud.CloudStorageManager;
import com.ubtedu.ukit.common.locale.LanguageUtil;
import com.ubtedu.ukit.common.utils.AnimatorHelper;
import com.ubtedu.ukit.menu.settings.Settings;
import com.ubtedu.ukit.user.UserManager;


/**
 * @Author qinicy
 * @Date 2018/11/6
 **/
@SuppressLint("Registered")
public class UKitApplication extends UbtBaseApplication {

    private static final String DEVICE_TOKEN_SP_KEY = "ukit_device_token";
    private static UKitApplication sInstance;
    private String mDeviceToken;

    @Override
    public void onCreate() {
        sInstance = this;
        super.onCreate();
//        LogUtil.d("");
        generateDeviceToken();

        FileHelper.init(this);
        ExceptionHelper.init(this);
        NetworkManager.getInstance().init(this);
        UserManager.getInstance().init(this);
        if (Build.VERSION.SDK_INT < 29) {
            CloudStorageManager.getInstance().init(this);
        }
        OtaHelper.init();
        AnimatorHelper.init();//设置动画播放时长为1.0，覆盖开发者选项中的设置
        URoSDK.getInstance().init(this);

        //必须放在最后一行
        initCrashManager();
    }


    @Override
    public Context getApplicationContext() {
        LanguageUtil.getInstance().updateConfiguration();
        return super.getApplicationContext();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Settings.init(this);
        LanguageUtil.init(this);
        LanguageUtil.getInstance().updateConfiguration();
    }


    private void initCrashManager() {
        UBTCrashManager.getInstance().init(this).startCatcher();
    }


    public static UKitApplication getInstance() {
        LanguageUtil.getInstance().updateConfiguration();
        return sInstance;
    }

    @Override
    public EventDelegate createEventDelegate() {
        return new BasicEventDelegate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        LogUtil.d("onLowMemory");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        LogUtil.d("level:" + level);
        if (level >= ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL) {

        } else {
            System.gc();
        }
    }


    /**
     * 生成Android设备唯一ID
     */
    public String generateDeviceToken() {
        if (mDeviceToken != null) {
            return mDeviceToken;
        }
        String dToken = SharedPreferenceUtils.getInstance(this)
                .getStringValue(DEVICE_TOKEN_SP_KEY, "");
        if (TextUtils.isEmpty(dToken)) {
            dToken = AppUtil.getDeviceUUID(this);
            SharedPreferenceUtils.getInstance(this).setValue(DEVICE_TOKEN_SP_KEY, dToken);
        }
        mDeviceToken = dToken;
        return mDeviceToken;
    }

}
