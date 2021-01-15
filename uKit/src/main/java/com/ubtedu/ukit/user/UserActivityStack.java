package com.ubtedu.ukit.user;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理activity
 * Created by qinicy on 2017/7/3.
 */

public class UserActivityStack {
    private static List<WeakReference<Activity>> mActivities = new ArrayList<>();


    public static boolean add(Activity activity) {
        if (!isContains(activity)) {
            return mActivities.add(new WeakReference<>(activity));
        }
        return true;
    }

    public static boolean remove(Activity activity) {
        for (WeakReference<Activity> wef : mActivities) {
            Activity a = wef.get();
            if (a != null && a == activity) {
                return mActivities.remove(wef);
            }
        }
        return true;
    }

    public static void finishAll() {
        for (WeakReference<Activity> wef : mActivities) {
            Activity a = wef.get();
            if (a != null) {
                a.finish();
            }
        }
    }

    private static boolean isContains(Activity activity) {
        for (WeakReference<Activity> wef : mActivities) {
            Activity a = wef.get();
            if (a != null && a == activity) {
                return true;
            }
        }
        return false;
    }
}
