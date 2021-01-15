package com.ubtedu.alpha1x.core.base.application;

import android.app.Application;
import android.content.res.Configuration;

import com.ubtedu.alpha1x.core.base.delegate.EventDelegate;
import com.ubtedu.alpha1x.core.base.delegate.IEventDelegateWrapper;

/**
 *
 */
public class UbtBaseApplication extends Application implements IEventDelegateWrapper {
    private EventDelegate mEventDelegate;

    @Override
    public void onCreate() {
        super.onCreate();
        mEventDelegate = getEventDelegate();
        if (mEventDelegate != null) {
            registerActivityLifecycleCallbacks(mEventDelegate);
            mEventDelegate.onApplicationCreate(this);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (mEventDelegate != null) {
            unregisterActivityLifecycleCallbacks(mEventDelegate);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getEventDelegate() != null) {
            getEventDelegate().onApplicationConfigurationChanged(newConfig);
        }
    }

    public EventDelegate createEventDelegate() {
        return null;
    }

    @Override
    public EventDelegate getEventDelegate() {
        if (mEventDelegate == null) {
            mEventDelegate = createEventDelegate();
        }
        return mEventDelegate;
    }
}
