package com.ubtedu.ukit.application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Process;

import com.ubtedu.alpha1x.utils.AppUtil;
import com.ubtedu.alpha1x.utils.LogUtil;

/**
 * @Author qinicy
 * @Date 2019/6/13
 **/
public class LocaleChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_LOCALE_CHANGED.equals(intent.getAction())) {
            LogUtil.d("Locale changed:" + AppUtil.getSystemLanguage().toString());
            Process.killProcess(Process.myPid());
        }
    }
}
