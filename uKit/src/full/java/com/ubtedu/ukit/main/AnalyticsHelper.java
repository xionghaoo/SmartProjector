package com.ubtedu.ukit.main;

import android.app.Activity;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.manager.SupportRequestManagerFragment;
import com.ubtedu.alpha1x.core.base.IExtraInfo;
import com.ubtedu.alpha1x.core.base.delegate.EventDelegate;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.ukit.common.analysis.PageNames;
import com.ubtrobot.analytics.mobile.AnalyticsKit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class AnalyticsHelper {
    private static final int MAX_EVENT = 100;
    private HashMap<String, EventDelegate.Event> mStartEventsMap;
    /**
     * APP切到后台的时间点。在统计页面或模块使用时长时，需要减掉APP切到后台的时间。
     */
    private long mAppBeginHiddenTime;


    private Map<Class<? extends Activity>, String> mReportActivities;

    {
        mReportActivities = new HashMap<>();

    }

    private Set<Class> mIgnoreClassSet;

    {
        mIgnoreClassSet = new HashSet<>();

        mIgnoreClassSet.add(SupportRequestManagerFragment.class);
    }

    public AnalyticsHelper() {
        mStartEventsMap = new HashMap<>();
    }

    public void trackBeginEvent(EventDelegate.Event event) {
        if (mStartEventsMap.size() > MAX_EVENT) {
            mStartEventsMap.clear();
        }
        if (event != null && event.sessionStart) {
            mStartEventsMap.put(event.id, event);
        }
    }

    public void trackEndEvent(EventDelegate.Event event) {
        if (event != null && !event.sessionStart) {
            EventDelegate.Event startEvent = mStartEventsMap.get(event.id);

            if (startEvent != null) {

                long duration = event.recordTime - startEvent.recordTime - startEvent.getDirtyTotalTime();
                if (duration > 0) {
                    AnalyticsKit.recordEvent(event.id, duration, event.keyStrings);
                }
                LogUtil.d("Event ID:" + event.id + "  duration:" + duration);
                mStartEventsMap.remove(event.id);
            }
        }
    }

    public void onAppVisibilityChangedToUser(boolean visible) {
        if (!visible) {
            mAppBeginHiddenTime = System.currentTimeMillis();
        } else {
            if (mAppBeginHiddenTime != 0) {
                long appHiddenTime = System.currentTimeMillis() - mAppBeginHiddenTime;
                Iterator<EventDelegate.Event> iterator = mStartEventsMap.values().iterator();
                while (iterator.hasNext()) {
                    EventDelegate.Event beginEvent = iterator.next();
                    beginEvent.addDirtyTime(appHiddenTime);
                }
            }
        }
    }

    /**
     * 按照优先顺序：
     * 1.从{@link PageNames#PAGE_MAP}取\n
     * 2.从接口IExtraInfo取\n
     * 3.class名
     *
     * @param activity
     * @return
     */
    public String getPageName(Activity activity) {
        String pageName = getPageName(activity.getClass());
        if (TextUtils.isEmpty(pageName)) {
            if (activity instanceof IExtraInfo) {
                IExtraInfo extraInfo = (IExtraInfo) activity;
                pageName = extraInfo.getExtraInfo();

            }
        }
        if (TextUtils.isEmpty(pageName)) {
            pageName = activity.getClass().getName();
        }
        return pageName;
    }

    /**
     * 按照优先顺序：
     * 1.从{@link PageNames#PAGE_MAP}取\n
     * 2.从接口IExtraInfo取\n
     * 3.class名
     *
     * @param fragment
     * @return
     */
    public String getPageName(Fragment fragment) {
        String pageName = getPageName(fragment.getClass());
        if (TextUtils.isEmpty(pageName)) {
            if (fragment instanceof IExtraInfo) {
                IExtraInfo extraInfo = (IExtraInfo) fragment;
                pageName = extraInfo.getExtraInfo();
            }
        }
        if (TextUtils.isEmpty(pageName)) {
            pageName = fragment.getClass().getName();
        }
        return pageName;
    }

    public String getPageName(Class clazz) {
        return PageNames.getPageName(clazz);
    }

    public String getReportDurationEventId(Class activity) {
        return mReportActivities.get(activity);
    }

    public boolean isIgnore(Class clazz) {
        return mIgnoreClassSet.contains(clazz);
    }
}
