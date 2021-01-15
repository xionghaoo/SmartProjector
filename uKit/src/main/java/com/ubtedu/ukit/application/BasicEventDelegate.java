/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.application;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;

import com.ubtedu.alpha1x.core.base.delegate.EventDelegate;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.ukit.common.analysis.LoginType;
import com.ubtedu.ukit.common.analysis.UBTReporter;
import com.ubtedu.ukit.common.base.UKitBaseActivity;
import com.ubtedu.ukit.launcher.EmptyLauncherActivity;
import com.ubtedu.ukit.project.bridge.api.ToastHelper;
import com.ubtedu.ukit.project.imported.ImportActivity;
import com.ubtedu.ukit.user.vo.UserInfo;

/**
 * @Author qinicy
 * @Date 2018/11/6
 **/
public class BasicEventDelegate extends EventDelegate {
    private static boolean isAppVisible = true;
    private static int sActivityCount = 0;
    private boolean isAppRestartBySystem;
    private boolean isLauncherComplete;
    private boolean isRegionChangedRecreate;
    private UKitBaseActivity mResumeActivity;

    @Override
    public void onApplicationCreate(Application application) {
        super.onApplicationCreate(application);
        UBTReporter.init(application, this);

        IntentFilter filter = new IntentFilter(Intent.ACTION_LOCALE_CHANGED);
        application.registerReceiver(new LocaleChangeReceiver(),filter);
    }

    @Override
    public void onApplicationConfigurationChanged(Configuration newConfig) {
        super.onApplicationConfigurationChanged(newConfig);
        LogUtil.d("");
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);
        sActivityCount++;
        //如果已经知道是系统恢复流程，就不需要更新该变量了
        //EmptyLauncherActivity是Launcher，所以sActivityCount = 1，其它activity的sActivityCount >= 2,
        //其它如果activity的sActivityCount < 2,就可以判断app是从系统中恢复的。
        if (!(activity instanceof EmptyLauncherActivity
                || activity instanceof ImportActivity)
                && !isAppRestartBySystem) {
            isAppRestartBySystem = sActivityCount < 2;
        }
        LogUtil.d("sActivityCount:" + sActivityCount + "  activity:" + activity.getClass().getSimpleName() + " isAppRestartBySystem:" + isAppRestartBySystem);
    }

    public void onAppVisibilityChangedToUser(boolean visible) {
        isAppVisible = visible;
    }

    public boolean isAppVisible() {
        return isAppVisible;
    }

    public boolean isAppRestartBySystem() {
        return isAppRestartBySystem;
    }


    public void setAppRestartBySystem(boolean appRestartBySystem) {
        isAppRestartBySystem = appRestartBySystem;
        sActivityCount = 2;
    }

    public void noticeAppUpdateConfiguration() {
        sActivityCount = 0;
    }

    public boolean isLauncherComplete() {
        return isLauncherComplete;
    }

    public void setLauncherComplete(boolean launcherComplete) {
        isLauncherComplete = launcherComplete;
    }

    public boolean isRegionChangedRecreate() {
        return isRegionChangedRecreate;
    }

    public void setRegionChangedRecreate(boolean regionChangedRecreate) {
        isRegionChangedRecreate = regionChangedRecreate;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (activity instanceof UKitBaseActivity) {
            mResumeActivity = (UKitBaseActivity) activity;
            ToastHelper.updateActivityUIDelegate(mResumeActivity.getUIDelegate());
        }
        super.onActivityResumed(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (activity == mResumeActivity) {
            mResumeActivity = null;
        }
        super.onActivityPaused(activity);
    }

    public UKitBaseActivity getResumeActivity() {
        return mResumeActivity;
    }

    public void onLoginStateChange(UserInfo user, LoginType type){

    }
}
