package com.ubtedu.ukit.common.ota;

import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.alpha1x.utils.SharedPreferenceUtils;
import com.ubtedu.ukit.BuildConfig;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.common.view.badge.BadgeView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Author qinicy
 * @Date 2019/6/5
 **/
public class APPUpgradeBadgeNotifier {
    private static List<WeakReference<BadgeView>> mBadges;

    static {
        mBadges = new ArrayList<>();
    }

    public static void addBadge(BadgeView badge) {
        if (!exist(badge)) {
            mBadges.add(new WeakReference<>(badge));
        }
        update();
    }

    private static boolean exist(BadgeView badge) {
        Iterator<WeakReference<BadgeView>> iterator = mBadges.iterator();
        while (iterator.hasNext()) {
            WeakReference<BadgeView> badgeWef = iterator.next();
            if (badgeWef.get() == badge) {
                return true;
            }
        }
        return false;
    }

    public static void update() {
        LogUtil.i("");
        if (mBadges.size() > 0) {
            UpgradeInfo info = SharedPreferenceUtils.getInstance(UKitApplication.getInstance()).getObjectValue(UpgradeConstants.SP_UPGRADE_INFO, UpgradeInfo.class);
            boolean hasNewVersion = false;
            if (info != null && info.versionCode >= BuildConfig.VERSION_CODE) {
                hasNewVersion = true;
            }
            LogUtil.i("hasNewVersion:" + hasNewVersion);
            Iterator<WeakReference<BadgeView>> iterator = mBadges.iterator();
            while (iterator.hasNext()) {
                WeakReference<BadgeView> badgeWef = iterator.next();
                if (badgeWef.get() != null) {
                    if (hasNewVersion) {
                        badgeWef.get().show();
                    } else {
                        badgeWef.get().hide();
                    }
                } else {
                    iterator.remove();
                }
            }
        }
    }

    public static void clear() {
        mBadges.clear();
    }
}
