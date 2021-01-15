package com.ubtedu.alpha1x.core.base.delegate;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentManager;

public class EventDelegate extends FragmentManager.FragmentLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    private int mStartedCount;
    private int mStoppedCount;
    private boolean isAppVisibleToUser;

    private final String TAG = EventDelegate.class.getSimpleName();

    public boolean isDelegate() {
        return true;
    }

    private void updateAppVisibleToUserState() {
        boolean visible = mStartedCount > mStoppedCount;
        if (visible != isAppVisibleToUser) {
            onAppVisibilityChangedToUser(visible);
            isAppVisibleToUser = visible;
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated:" + activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.i(TAG, "onActivityStarted:" + activity.getClass().getSimpleName());
        mStartedCount++;
        updateAppVisibleToUserState();

    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.i(TAG, "onActivityResumed:" + activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.i(TAG, "onActivityPaused:" + activity.getClass().getSimpleName());

    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.i(TAG, "onActivityStopped:" + activity.getClass().getSimpleName());
        mStoppedCount++;
        updateAppVisibleToUserState();

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.i(TAG, "onActivityDestroyed:" + activity.getClass().getSimpleName());
    }

    @Override
    public void onFragmentPreAttached(FragmentManager fm, Fragment f, Context context) {
        super.onFragmentPreAttached(fm, f, context);
        if (f.getClass().getSimpleName().equals("SupportRequestManagerFragment")) {
            return;
        }

    }

    @Override
    public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
        super.onFragmentAttached(fm, f, context);
        if (f.getClass().getSimpleName().equals("SupportRequestManagerFragment")) {
            return;
        }

    }

    @Override
    public void onFragmentPreCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
        super.onFragmentPreCreated(fm, f, savedInstanceState);
        if (f.getClass().getSimpleName().equals("SupportRequestManagerFragment")) {
            return;
        }

    }

    @Override
    public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
        super.onFragmentCreated(fm, f, savedInstanceState);

        if (f.getClass().getSimpleName().equals("SupportRequestManagerFragment")) {
            return;
        }
        Log.i(TAG, "onFragmentCreated:" + f.getClass().getSimpleName());
    }

    @Override
    public void onFragmentActivityCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
        super.onFragmentActivityCreated(fm, f, savedInstanceState);
        if (f.getClass().getSimpleName().equals("SupportRequestManagerFragment")) {
            return;
        }

    }

    @Override
    public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
        super.onFragmentViewCreated(fm, f, v, savedInstanceState);

        if (f.getClass().getSimpleName().equals("SupportRequestManagerFragment")) {
            return;
        }
        Log.i(TAG, "onFragmentViewCreated:" + f.getClass().getSimpleName());
    }

    @Override
    public void onFragmentStarted(FragmentManager fm, Fragment f) {
        super.onFragmentStarted(fm, f);
        if (f.getClass().getSimpleName().equals("SupportRequestManagerFragment")) {
            return;
        }
        Log.i(TAG, "onFragmentStarted:" + f.getClass().getSimpleName());
    }

    @Override
    public void onFragmentResumed(FragmentManager fm, Fragment f) {
        super.onFragmentResumed(fm, f);
        if (f.getClass().getSimpleName().equals("SupportRequestManagerFragment")) {
            return;
        }
        Log.i(TAG, "onFragmentResumed:" + f.getClass().getSimpleName());
    }


    @Override
    public void onFragmentPaused(FragmentManager fm, Fragment f) {
        super.onFragmentPaused(fm, f);
        if (f.getClass().getSimpleName().equals("SupportRequestManagerFragment")) {
            return;
        }
        Log.i(TAG, "onFragmentPaused:" + f.getClass().getSimpleName());
    }

    @Override
    public void onFragmentStopped(FragmentManager fm, Fragment f) {
        super.onFragmentStopped(fm, f);
        if (f.getClass().getSimpleName().equals("SupportRequestManagerFragment")) {
            return;
        }
        Log.i(TAG, "onFragmentStopped:" + f.getClass().getSimpleName());

    }

    @Override
    public void onFragmentSaveInstanceState(FragmentManager fm, Fragment f, Bundle outState) {
        super.onFragmentSaveInstanceState(fm, f, outState);
        if (f.getClass().getSimpleName().equals("SupportRequestManagerFragment")) {
            return;
        }

    }

    @Override
    public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
        super.onFragmentViewDestroyed(fm, f);
        if (f.getClass().getSimpleName().equals("SupportRequestManagerFragment")) {
            return;
        }
        Log.i(TAG, "onFragmentViewDestroyed:" + f.getClass().getSimpleName());
    }

    @Override
    public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
        super.onFragmentDestroyed(fm, f);
        if (f.getClass().getSimpleName().equals("SupportRequestManagerFragment")) {
            return;
        }
        Log.i(TAG, "onFragmentDestroyed:" + f.getClass().getSimpleName());
    }

    @Override
    public void onFragmentDetached(FragmentManager fm, Fragment f) {
        super.onFragmentDetached(fm, f);
        if (f.getClass().getSimpleName().equals("SupportRequestManagerFragment")) {
            return;
        }
        Log.i(TAG, "onFragmentDetached:" + f.getClass().getSimpleName());
    }

    public void onPageStart(String pageName) {
        Log.i(TAG, "onPageStart:" + pageName);
    }

    public void onPageEnd(String pageName) {
        Log.i(TAG, "onPageEnd:" + pageName);
    }

    public void onAppVisibilityChangedToUser(boolean visible) {

    }

    public void onApplicationCreate(Application application) {

    }
    public void onApplicationConfigurationChanged(Configuration newConfig) {

    }
    public Context onActivityAttachBaseContext(Activity activity, Context base) {

        return null;
    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

    }

    public void onNewIntent(Activity activity, Intent intent) {

    }

    public void onActivityBackPressed(Activity activity) {

    }

    public void onThirdPartyLogin(Activity activity) {

    }


    public void onFragmentVisibilityChangedToUser(FragmentManager fm, Fragment f, boolean visible) {
        if (f.getClass().getSimpleName().equals("SupportRequestManagerFragment")) {
            return;
        }
        Log.i(TAG, "onFragmentVisibilityChangedToUser:" + f.getClass().getSimpleName() + " visible:" + visible);
    }

    public void onEvent(Context context, EventDelegate.Event event) {
    }

    public void onAction(Context context, IAction action) {
    }

    public void onShare(Context context, EventDelegate.Share share) {
    }

    public static class Event {
        /**
         * 脏时间数据，比如事件开始后，app进入了后台再回到前台的这段事件，就叫脏数据。
         */
        private List<Long> dirtyTimes;
        public int type;
        public String id;
        public String page;
        public boolean sessionStart;
        public Map<String, String> keyStrings = new HashMap<>();

        public long recordTime = System.currentTimeMillis();

        public long getDirtyTotalTime() {
            if (dirtyTimes == null) {
                return 0;
            }
            long totalTime = 0;
            Iterator<Long> iterator = dirtyTimes.iterator();
            while (iterator.hasNext()) {
                long time = iterator.next();
                totalTime += time;
            }
            return totalTime;
        }

        public void addDirtyTime(long time) {
            if (dirtyTimes == null) {
                dirtyTimes = new ArrayList<>(5);
            }
            dirtyTimes.add(time);
        }

        @Override
        public String toString() {
            String argsJson = "";

            String keyStringsJson = " ";
            if (keyStrings != null) {
                keyStringsJson = new JSONArray(Arrays.asList(keyStrings)).toString();
            }

            return "Event[type:" + type + " id:" + id + " sessionStart:" + sessionStart +
                    " args:" + argsJson +
                    " keyStrings:" + keyStringsJson + "]";

        }
    }

    public static class Share {
        public int type;
        public int scene;
        public String title;
        public String content;
        public String url;
        public int resourceId;
    }

    public interface IAction {

    }
}
