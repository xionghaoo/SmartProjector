package com.ubtrobot.smartprojector.utils

import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable

class PackageUtil {
    companion object {
        fun appList(activity: Activity) : List<AppMetaInfo> {
            val appList = ArrayList<AppMetaInfo>()
            // 获取已安装的app列表
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            activity.packageManager.apply {
                val packageList = queryIntentActivities(intent, 0)
                packageList.forEach { info ->
                    val appInfo: ApplicationInfo = getApplicationInfo(info.activityInfo.packageName, 0)
                    val appName = getApplicationLabel(appInfo).toString()
                    val icon = getApplicationIcon(appInfo)
                    appList.add(AppMetaInfo(appInfo.packageName, appName, icon))
                }
            }
            return appList
        }

        fun startApp(activity: Activity, pkgName: String) {
            activity.packageManager.apply {
                val launchIntent = getLaunchIntentForPackage(pkgName)
                activity.startActivity(launchIntent)
            }
        }
    }
}

data class AppMetaInfo(
    val packageName: String,
    val appName: String?,
    val icon: Drawable?
)