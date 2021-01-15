package com.ubtedu.base.net.manager;

/**
 * Created by qinicy on 2017/5/31.
 */

import android.net.NetworkInfo;

/**
 * 网络状态观察者
 * Created by yinglovezhuzhu@gmail.com on 2015/6/11.
 */
public abstract class NetworkObserver {

    /**
     * 网络状态发生改变
     * @param networkConnected 是否没有连接，比如：没有网络
     * @param currentNetwork 当前网络连接信息，没有为null
     * @param lastNetwork 上一个网络连接信息，没有为null
     */
    public abstract void onNetworkStateChanged(boolean networkConnected, NetworkInfo currentNetwork,
                                               NetworkInfo lastNetwork);
}