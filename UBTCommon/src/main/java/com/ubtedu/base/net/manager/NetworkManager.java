package com.ubtedu.base.net.manager;

/**
 * Created by qinicy on 2017/5/31.
 */


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络状态管理类
 * Created by yinglovezhuzhu@gmail.com on 2015/7/23.
 */
public class NetworkManager {

    private Context mContext;

    private ConnectivityManager mConnectivityManager;

    private final NetworkObservable mNetworkObservable = new NetworkObservable();

    private boolean mNetworkConnected = false;

    private NetworkInfo mCurrentNetwork = null;

    private boolean mInitialized = false;

    private static NetworkManager mInstance = null;

    private NetworkManager() {

    }

    public static NetworkManager getInstance() {
        synchronized (NetworkManager.class) {
            if (null == mInstance) {
                mInstance = new NetworkManager();
            }
            return mInstance;
        }
    }

    public void init(Context context) {
        if (!mInitialized) {
            mContext = context.getApplicationContext();
            mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            mContext.registerReceiver(new NetworkReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            mInitialized = true;
        }

        // 初始化网络状态
        mCurrentNetwork = mConnectivityManager.getActiveNetworkInfo();
        mNetworkConnected = null != mCurrentNetwork && mCurrentNetwork.isConnected();
    }

    /**
     * 网络是否连接
     *
     * @return
     */
    public boolean isNetworkConnected() {
        if (mConnectivityManager != null) {
            mCurrentNetwork = mConnectivityManager.getActiveNetworkInfo();
            mNetworkConnected = null != mCurrentNetwork && mCurrentNetwork.isConnected();
        }
        return mNetworkConnected;
    }

    /**
     * 获取当前网络信息
     *
     * @return 当前网络信息，如果无网络连接，则为null
     */
    public NetworkInfo getCurrentNetwork() {
        return mCurrentNetwork;
    }

    /**
     * 注册一个网络状态观察者
     *
     * @param observer
     */
    public void registerNetworkObserver(NetworkObserver observer) {
        synchronized (mNetworkObservable) {
            try {
                mNetworkObservable.registerObserver(observer);
            } catch (IllegalStateException e) {
            }
        }
    }

    /**
     * 反注册一个网络状态观察者
     *
     * @param observer
     */
    public void unregisterNetworkObserver(NetworkObserver observer) {
        synchronized (mNetworkObservable) {
            try {
                mNetworkObservable.unregisterObserver(observer);
            } catch (IllegalStateException e) {
            }
        }
    }

    /**
     * 反注册所有的观察者，建议这个只在退出程序时做清理用
     */
    public void unregisterAllObservers() {
        synchronized (mNetworkObservable) {
            mNetworkObservable.unregisterAll();
        }
    }


    private class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent) {
                return;
            }
            if (!ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                return;
            }

            NetworkInfo lastNetwork = mCurrentNetwork;
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            mNetworkConnected = !noConnectivity;
            if (mNetworkConnected) {
                mCurrentNetwork = mConnectivityManager.getActiveNetworkInfo();
            } else {
                mCurrentNetwork = null;
                // 没有网络连接，直接返回
            }

            mNetworkObservable.notifyNetworkChanged(mNetworkConnected, mCurrentNetwork, lastNetwork);
        }
    }
}
