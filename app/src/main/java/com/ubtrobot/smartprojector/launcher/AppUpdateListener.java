package com.ubtrobot.smartprojector.launcher;

import java.util.List;

public interface AppUpdateListener {
    boolean onAppUpdated(List<App> apps);
}
