package com.ubtedu.ukit.main;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.multidex.MultiDex;

import com.ubtedu.alpha1x.core.base.delegate.EventDelegate;
import com.ubtedu.ukit.application.UKitApplication;

/**
 * @author qinicy
 * @data 2017/11/17
 */
@Keep
public class UKitFullApplication extends UKitApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }


    @Override
    public EventDelegate createEventDelegate() {
        return new UKitFullEventDelegate();
    }
}
