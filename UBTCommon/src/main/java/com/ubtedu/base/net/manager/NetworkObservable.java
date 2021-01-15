package com.ubtedu.base.net.manager;

/**
 * Created by qinicy on 2017/5/31.
 */
import android.net.NetworkInfo;

/**
 * 网络状态被观察者
 * Created by yinglovezhuzhu@gmail.com on 2015/6/11.
 */
public class NetworkObservable extends Observable<NetworkObserver> {

    public void notifyNetworkChanged(boolean networkConnected, NetworkInfo currentNetwok,
                                     NetworkInfo lastNetwork) {
        synchronized (mObservers) {
            for(int i = mObservers.size() - 1; i >= 0; i--) {
                NetworkObserver observer = mObservers.get(i);
                observer.onNetworkStateChanged(networkConnected, currentNetwok, lastNetwork);
            }
        }
    }
}