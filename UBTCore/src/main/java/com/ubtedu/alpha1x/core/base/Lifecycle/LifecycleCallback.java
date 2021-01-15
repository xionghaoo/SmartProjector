/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.alpha1x.core.base.Lifecycle;

import android.content.Intent;

import androidx.annotation.Keep;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

/**
 * LifecycleObserver includes lifecycle events
 * @Author: qinicy
 * @Date: 2018/9/3 18:09
 **/
public interface LifecycleCallback extends LifecycleObserver {
    @Keep
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onStart();
    @Keep
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreate();
    @Keep
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResume();

    @Keep
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause();
    @Keep
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onStop();
    @Keep
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy();

    void onActivityResult(int requestCode, int resultCode, Intent data);
}