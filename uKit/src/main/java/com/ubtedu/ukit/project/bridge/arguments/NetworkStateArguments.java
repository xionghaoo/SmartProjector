package com.ubtedu.ukit.project.bridge.arguments;

import android.net.ConnectivityManager;

import com.ubtedu.base.net.manager.NetworkManager;

public class NetworkStateArguments {

    private static final int NETWORK_STATE_NONE = 0;
    private static final int NETWORK_STATE_MOBILE = 1;
    private static final int NETWORK_STATE_WIFI = 2;

    /**
     * 获取网络状态
     * @return 无网络返回0，wifi返回2，其他返回1
     */
    public static int getNetworkState() {
        if (NetworkManager.getInstance().isNetworkConnected()) {
            if (NetworkManager.getInstance().getCurrentNetwork().getType() == ConnectivityManager.TYPE_WIFI) {
                return NETWORK_STATE_WIFI;
            } else {
                return NETWORK_STATE_MOBILE;
            }
        } else {
            return NETWORK_STATE_NONE;
        }
    }
}
