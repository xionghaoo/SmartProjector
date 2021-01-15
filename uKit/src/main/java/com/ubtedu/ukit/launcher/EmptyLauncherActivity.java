/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.launcher;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.ubtedu.alpha1x.utils.AppUtil;
import com.ubtedu.ukit.BuildConfig;
import com.ubtedu.ukit.application.PrivacyInfo;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.common.analysis.Events;
import com.ubtedu.ukit.common.analysis.UBTReporter;
import com.ubtedu.ukit.common.base.UKitBaseActivity;
import com.ubtedu.ukit.common.flavor.Flavor;
import com.ubtedu.ukit.home.HomeActivity;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @Author qinicy
 * @Date 2019/1/14
 **/
public class EmptyLauncherActivity extends UKitBaseActivity {
    private Handler mHandler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null && !isTaskRoot()
                && (intent.getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }

        mHandler = new Handler();

        reportAPPStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                HomeActivity.open(EmptyLauncherActivity.this);
                finish();
            }
        },150);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacksAndMessages(null);
    }
    private void reportAPPStart(){
        Map<String,String> args = new HashMap<>();
        args.put("app_id", String.valueOf(PrivacyInfo.getUBTAppId()));
        args.put("app_version", BuildConfig.VERSION_NAME);
        args.put("channel", Flavor.getChannel().name());
        args.put("deviceID", UKitApplication.getInstance().generateDeviceToken());
        args.put("deviceModel",Build.MODEL);
        args.put("os_version", String.valueOf(Build.VERSION.SDK_INT));
        args.put("os_language", AppUtil.getSystemLanguage().getDisplayLanguage());
        args.put("os_time_zone", new SimpleDateFormat("ZZZZ", Locale.getDefault()).format(System.currentTimeMillis()));
        UBTReporter.onEvent(Events.Ids.app_launcher,args);
    }
}
