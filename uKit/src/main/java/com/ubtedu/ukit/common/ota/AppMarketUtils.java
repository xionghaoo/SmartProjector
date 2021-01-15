package com.ubtedu.ukit.common.ota;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.ukit.BuildConfig;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.common.flavor.Channel;
import com.ubtedu.ukit.project.bridge.api.ActivityHelper;

import java.util.List;


/**
 * Bugly的版本配置一定要和对应市场版本同步更新
 *
 * @Author qinicy
 * @Date 2019/6/11
 **/
public class AppMarketUtils {
    private static Application sApplication = UKitApplication.getInstance();
    private static String GOOGLE_PLAY_URI = "https://play.google.com/store/apps/details?id=";
    private static String MARKET_URI = "market://details?id=";

    public static boolean isMarketInstalled(Channel channel) {
        if (channel == Channel.ubtedu) {
            return false;
        }
        boolean installed = isIntentAvailable(sApplication, createMarketIntent(channel));
        //FUCK:联想渣渣PAD应用商店和手机版不一样
        if (!installed && channel == Channel.lenovo) {
            installed = isIntentAvailable(sApplication, createMarketIntent(Channel.lenovoPad));
        }
        return installed;
    }


    public static void openMarket(Channel channel) {

        Intent intent = createMarketIntent(channel);
        Context context = ActivityHelper.getResumeActivity();
        if (context == null) {
            context = sApplication;
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (isIntentAvailable(context, intent)) {
            context.startActivity(intent);
        } else {
            intent.setPackage(null);
            if (isIntentAvailable(context, intent)) {
                context.startActivity(intent);
            } else {
                LogUtil.e("Cannot open market:" + channel.name());
            }
        }
    }

    private static Intent createMarketIntent(Channel channel) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(getMarketUri(channel));
        intent.setPackage(getMarketPackage(channel));
        return intent;
    }

//    private static Uri getMarketUri(Channel channel) {
//        if (channel == Channel.googlePlay || channel == Channel.chromebook) {
//            return Uri.parse(GOOGLE_PLAY_URI + BuildConfig.APPLICATION_ID);
//        } else {
//            return Uri.parse(MARKET_URI + BuildConfig.APPLICATION_ID);
//        }
//
//    }

    private static String getMarketPackage(Channel channel) {
        switch (channel) {
            case googlePlay:
            case chromebook:
                return APPMarketPackages.PACKAGE_GOOGLEPLAY_MARKET;
            case huawei:
                return APPMarketPackages.PACKAGE_HUAWEI_MARKET;
            case tencent:
                return APPMarketPackages.PACKAGE_TENCENT_MARKET;
            case wandoujia:
                return APPMarketPackages.PACKAGE_WANDOUJIA_MARKET;
            case lenovo:
                return APPMarketPackages.PACKAGE_LENOVO_MARKET;
            case lenovoPad:
                return APPMarketPackages.PACKAGE_LENOVO_PAD_MARKET;
            case qihoo:
                return APPMarketPackages.PACKAGE_360_MARKET;
        }
        //Channel.other return null
        return null;
    }

    @SuppressLint("WrongConstant")
    public static boolean isIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.GET_ACTIVITIES);
        return list.size() > 0;
    }

}
