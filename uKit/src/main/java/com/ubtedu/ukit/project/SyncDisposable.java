/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project;

import io.reactivex.disposables.Disposable;

/**
 * @Author qinicy
 * @Date 2019/3/6
 **/
public class SyncDisposable implements Disposable {
    private boolean isOnlineSync;
    private Disposable mSource;

    public SyncDisposable(boolean isOnlineSync, Disposable source) {
        this.isOnlineSync = isOnlineSync;
        mSource = source;
    }

    public boolean isOnlineSync() {
        return isOnlineSync;
    }

    @Override
    public void dispose() {
        mSource.dispose();
    }

    @Override
    public boolean isDisposed() {
        return mSource.isDisposed();
    }
}
