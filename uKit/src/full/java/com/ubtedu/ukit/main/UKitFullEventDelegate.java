package com.ubtedu.ukit.main;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.bugly.beta.download.DownloadTask;
import com.tencent.bugly.beta.upgrade.UpgradeListener;
import com.ubtedu.alpha1x.core.base.delegate.EventDelegate;
import com.ubtedu.alpha1x.utils.GsonUtil;
import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.alpha1x.utils.SharedPreferenceUtils;
import com.ubtedu.ukit.BuildConfig;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.BasicEventDelegate;
import com.ubtedu.ukit.application.PrivacyInfo;
import com.ubtedu.ukit.application.ServerConfig;
import com.ubtedu.ukit.application.ServerEnv;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.common.actions.APPUpgradeAction;
import com.ubtedu.ukit.common.actions.CheckUpgradeAction;
import com.ubtedu.ukit.common.analysis.Events;
import com.ubtedu.ukit.common.analysis.LoginType;
import com.ubtedu.ukit.common.analysis.UBTReporter;
import com.ubtedu.ukit.common.base.UKitBaseActivity;
import com.ubtedu.ukit.common.dialog.PromptDialogFragment;
import com.ubtedu.ukit.common.flavor.Flavor;
import com.ubtedu.ukit.common.ota.APPUpgradeBadgeNotifier;
import com.ubtedu.ukit.common.ota.AppMarketUtils;
import com.ubtedu.ukit.common.ota.UpgradeConstants;
import com.ubtedu.ukit.home.HomeActivity;
import com.ubtedu.ukit.menu.MenuActivity;
import com.ubtedu.ukit.project.bridge.api.ActivityHelper;
import com.ubtedu.ukit.user.UserManager;
import com.ubtedu.ukit.user.vo.UserInfo;
import com.ubtrobot.analytics.mobile.AnalyticsKit;

import java.util.HashMap;
import java.util.Map;

import static com.ubtedu.ukit.common.analysis.Events.Ids.EVENT_PAGE_END;
import static com.ubtedu.ukit.common.analysis.Events.Ids.EVENT_PAGE_START;

/**
 * @author qinicy
 * @data 2019/02/20
 */

public class UKitFullEventDelegate extends BasicEventDelegate {

    private UKitApplication mApplication;
    private HomeActivity mActivity;
    private AnalyticsHelper mAnalyticsHelper;

    @Override
    public void onApplicationCreate(Application application) {
        super.onApplicationCreate(application);
        mApplication = UKitApplication.getInstance();
        initBuglySdk();
        initUbtAnalyticsKit();
        initLaunchWork();
    }
    private void initLaunchWork() {
        initUpgradeInfo();
    }

    /**
     * 如果启动的时候发现当前版本和上次运行的版本不一致，说明是刚升级后的第一次启动，需要清除升级信息。
     */
    private void initUpgradeInfo() {
        int versionCode = SharedPreferenceUtils.getInstance(mApplication).getIntValue(UpgradeConstants.SP_APP_LAUNCHER_VERSION);
        if (versionCode != BuildConfig.VERSION_CODE) {
            clearUpgradeInfo();
        }
        SharedPreferenceUtils.getInstance(mApplication).setValue(UpgradeConstants.SP_APP_LAUNCHER_VERSION, BuildConfig.VERSION_CODE);
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);
        if (activity instanceof HomeActivity) {
            onHomeActivityCreated(activity);
        }


        //统计功能模块使用时间
        String eventId = mAnalyticsHelper.getReportDurationEventId(activity.getClass());
        if (!TextUtils.isEmpty(eventId)) {
            Event event = new Event();
            event.id = eventId;
            event.type = Events.TYPE_CALCULATE;
            event.sessionStart = true;
            onEvent(activity, event);
        }
    }


    @Override
    public void onActivityResumed(Activity activity) {
        super.onActivityResumed(activity);
        if (!mAnalyticsHelper.isIgnore(activity.getClass())) {
            AnalyticsKit.recordActivityStart(mAnalyticsHelper.getPageName(activity));
        }

    }


    @Override
    public void onActivityPaused(Activity activity) {
        super.onActivityPaused(activity);
        if (!mAnalyticsHelper.isIgnore(activity.getClass())) {
            AnalyticsKit.recordActivityEnd(mAnalyticsHelper.getPageName(activity));
        }
    }


    @Override
    public void onActivityDestroyed(Activity activity) {
        super.onActivityDestroyed(activity);
        if (activity instanceof HomeActivity) {
            onHomeActivityDestroyed(activity);
        }
        //统计功能模块使用时间
        String eventId = mAnalyticsHelper.getReportDurationEventId(activity.getClass());
        if (!TextUtils.isEmpty(eventId)) {
            EventDelegate.Event event = new EventDelegate.Event();
            event.id = eventId;
            event.type = Events.TYPE_CALCULATE;
            event.sessionStart = false;
            onEvent(activity, event);
        }
    }


    @Override
    public void onFragmentVisibilityChangedToUser(FragmentManager fm, Fragment f, boolean visible) {
        super.onFragmentVisibilityChangedToUser(fm, f, visible);

        if (!mAnalyticsHelper.isIgnore(f.getClass())) {
            if (visible) {
                AnalyticsKit.recordFragmentStart(mAnalyticsHelper.getPageName(f));
            } else {
                AnalyticsKit.recordFragmentEnd(mAnalyticsHelper.getPageName(f));
            }
        }
    }

    @Override
    public void onAppVisibilityChangedToUser(boolean visible) {
        super.onAppVisibilityChangedToUser(visible);

        mAnalyticsHelper.onAppVisibilityChangedToUser(visible);
    }

    @Override
    public void onPageStart(String pageName) {
        super.onPageStart(pageName);
        AnalyticsKit.recordViewStart(pageName);
    }

    @Override
    public void onPageEnd(String pageName) {
        super.onPageEnd(pageName);
        AnalyticsKit.recordViewEnd(pageName);
    }

    @Override
    public void onEvent(Context context, EventDelegate.Event event) {
        if (event == null) {
            return;
        }
        LogUtil.d(event.toString());
        if (EVENT_PAGE_START.equals(event.id)) {
            onPageStart(event.page);
            return;
        }
        if (EVENT_PAGE_END.equals(event.id)) {
            onPageEnd(event.page);
            return;
        }
        if (event.type == Events.TYPE_COUNT) {
            AnalyticsKit.recordEvent(event.id, event.keyStrings);
        }

        if (event.type == Events.TYPE_CALCULATE) {
            if (event.sessionStart) {
                mAnalyticsHelper.trackBeginEvent(event);
            } else {
                mAnalyticsHelper.trackEndEvent(event);
            }
        }
    }

    @Override
    public void onAction(Context context, IAction action) {
        super.onAction(context, action);
        if (action instanceof APPUpgradeAction) {
            upgradeAPP();
        } else if (action instanceof CheckUpgradeAction) {
            checkUpgrade();
        }
    }

    private void checkUpgrade() {
        Beta.checkUpgrade(false, false);
    }

    private void upgradeAPP() {

        if (Beta.getUpgradeInfo() != null) {
            DownloadTask task = Beta.startDownload();
            if (task.getStatus() == DownloadTask.DOWNLOADING) {
                if (getResumeActivity() != null) {
                    getResumeActivity().getUIDelegate().toastLong(getResumeActivity().getString(R.string.menu_upgrade_downloading));
                }
            }
        }
    }

    private void onUpgradeClick() {
        if (AppMarketUtils.isMarketInstalled(Flavor.getChannel())) {
            AppMarketUtils.openMarket(Flavor.getChannel());
        } else {
            upgradeAPP();
        }
    }

    private void onHomeActivityCreated(Activity activity) {
        mActivity = (HomeActivity) activity;
        AnalyticsKit.setUserId(UserManager.getInstance().getLoginUserId());
        AnalyticsKit.setDeviceInfo(UserManager.getInstance().getLoginUserId(), Flavor.getChannel().name());
        checkUpgrade();

        LogUtil.d("channel:" + Flavor.getChannel().name());
    }

    private void onHomeActivityDestroyed(Activity activity) {
        mActivity = null;
    }

    private void initBuglySdk() {
        Bugly.enable = true;
        Beta.autoCheckUpgrade = false;
        Beta.enableHotfix = false;
        Beta.canShowUpgradeActs.add(HomeActivity.class);
        Beta.canShowUpgradeActs.add(MenuActivity.class);
        Beta.upgradeDialogLayoutId = R.layout.dialog_app_upgrade;
        Beta.appChannel = Flavor.getChannel().name();
        Beta.appVersionCode = BuildConfig.VERSION_CODE;
        Beta.appVersionName = BuildConfig.VERSION_NAME;
        Beta.upgradeListener = new UpgradeListener() {
            @Override
            public void onUpgrade(int ret, UpgradeInfo info, boolean isManual, boolean isSilence) {
                onCheckUpgradeResult(ret, info);
            }
        };

        Bugly.init(mApplication, PrivacyInfo.getTencentBuglyAppId(), BuildConfig.DEBUG);
    }

    private void onCheckUpgradeResult(int ret, UpgradeInfo info) {
        String json = info == null ? "null" : GsonUtil.get().toJson(info);
        LogUtil.i("ret:" + ret + " info:" + json);
        if (ret == 0) {
            if (info != null) {
                handleUpgradeInfo(info);
            } else {
                clearUpgradeInfo();
                if (ActivityHelper.getResumeActivity() instanceof MenuActivity) {
                    ActivityHelper.getResumeActivity().getUIDelegate().toastShort(mApplication.getString(R.string.menu_upgrade_up_to_data));
                }
            }
        }
        APPUpgradeBadgeNotifier.update();
    }

    private void clearUpgradeInfo() {
        SharedPreferenceUtils.getInstance(mApplication).removeKey(UpgradeConstants.SP_UPGRADE_HISTORY);
        SharedPreferenceUtils.getInstance(mApplication).removeKey(UpgradeConstants.SP_UPGRADE_INFO);
    }

    private void handleUpgradeInfo(UpgradeInfo info) {
        com.ubtedu.ukit.common.ota.UpgradeInfo upgradeInfo = new com.ubtedu.ukit.common.ota.UpgradeInfo();
        upgradeInfo.versionName = info.versionName;
        upgradeInfo.versionCode = info.versionCode;
        upgradeInfo.packageUrl = info.apkUrl;
        upgradeInfo.packageMd5 = info.apkMd5;
        upgradeInfo.desc = info.newFeature;
        SharedPreferenceUtils.getInstance(mApplication).setValue(UpgradeConstants.SP_UPGRADE_INFO, upgradeInfo);

        UKitBaseActivity activity = ActivityHelper.getResumeActivity();
        if (activity != null) {
            final boolean isForced = info.upgradeType == 2;
            final boolean isHome = activity instanceof HomeActivity;

            if (isHome) {
                String history = SharedPreferenceUtils.getInstance(mApplication).getStringValue(UpgradeConstants.SP_UPGRADE_HISTORY, null);
                String id = null;
                int times = 0;
                if (history != null) {
                    String[] args = history.split(UpgradeConstants.UPGRADE_HISTORY_SPLIT);
                    if (args.length == 2) {
                        id = args[0];
                        try {
                            times = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                }
                if (id != null && !id.equals(info.id)) {
                    times = 0;
                }
                if (times >= info.popTimes && !isForced) {
                    return;
                }
                String newHistory = info.id + UpgradeConstants.UPGRADE_HISTORY_SPLIT + (times + 1);
                SharedPreferenceUtils.getInstance(mApplication).setValue(UpgradeConstants.SP_UPGRADE_HISTORY, newHistory);
            }

            showUpgradeDialog(info);
        }
    }

    private void showUpgradeDialog(UpgradeInfo info) {
        final UKitBaseActivity activity = ActivityHelper.getResumeActivity();
        if (activity != null) {
            final boolean isForced = info.upgradeType == 2;
            final boolean isHome = activity instanceof HomeActivity;
            boolean cancelAble = !isHome || !isForced;

            String version = info.versionName + "." + info.versionCode;
            String msg = activity.getString(R.string.app_upgradle_desc, version);
            PromptDialogFragment.newBuilder(activity)
                    .type(PromptDialogFragment.Type.NORMAL)
                    .title(activity.getString(R.string.app_upgradle_title))
                    .message(msg)
                    .positiveButtonText(activity.getString(R.string.menu_upgrade_positive_text))
                    .negativeButtonText(activity.getString(R.string.menu_upgrade_negative_text))
                    .showNegativeButton(cancelAble)
                    .cancelable(cancelAble)
                    .onPositiveClickListener(new PromptDialogFragment.OnConfirmClickListener() {
                        @Override
                        public boolean onClick() {
                            onUpgradeClick();
                            return isHome && isForced;
                        }
                    })
                    .build()
                    .show(activity.getSupportFragmentManager(), "showAppUpgradleDialogFragment");
        }

    }

    @Override
    public void onLoginStateChange(UserInfo user, LoginType type) {
        super.onLoginStateChange(user, type);
        updateAnalyticsKitUserInfo(user);
        reportLoginEvent(user,type);
    }

    private void updateAnalyticsKitUserInfo(UserInfo user){
        if (user != null){
            AnalyticsKit.setUserId(user.getUserID());
            AnalyticsKit.setDeviceInfo(user.getUserID(), Flavor.getChannel().name());
        }

    }
    private void reportLoginEvent(UserInfo user, LoginType type) {
        if (user != null && type != null) {
            Map<String, String> args = new HashMap<>();
            args.put("userId", user.getUserID());
            args.put("type", type.name());
            UBTReporter.onEvent(Events.Ids.app_login, args);
        }
    }
    private void initUbtAnalyticsKit() {
        UBTReporter.init(mApplication, this);
        mAnalyticsHelper = new AnalyticsHelper();
        AnalyticsKit.initialize(mApplication.getApplicationContext(), String.valueOf(PrivacyInfo.getUBTAppId()),
                PrivacyInfo.getAppKey(), mApplication.generateDeviceToken(), null);
        AnalyticsKit.enableDebug(!isReleaseEnv());
        AnalyticsKit.enable(true);
    }

    private boolean isReleaseEnv() {
        ServerEnv env = ServerConfig.getServerEnvConfig();
        return ServerEnv.RELEASE == env;
    }
}
