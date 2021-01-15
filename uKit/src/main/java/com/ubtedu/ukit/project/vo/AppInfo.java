package com.ubtedu.ukit.project.vo;

import android.graphics.drawable.Drawable;

public class AppInfo {

    private String appPackageName;
    private String appLauncherClassName;
    private String appName;
    private Drawable appIcon;

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public String getAppLauncherClassName() {
        return appLauncherClassName;
    }

    public void setAppLauncherClassName(String appLauncherClassName) {
        this.appLauncherClassName = appLauncherClassName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }
}
