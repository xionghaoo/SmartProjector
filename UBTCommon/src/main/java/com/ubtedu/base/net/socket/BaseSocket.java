package com.ubtedu.base.net.socket;

import android.os.Handler;
import android.os.Looper;

/**
 * https://github.com/Blankeer/XAndroidSocket
 */
public abstract class BaseSocket {
    private Handler mUIHandler;
    protected Object lock;

    public BaseSocket() {
        mUIHandler = new Handler(Looper.getMainLooper());
        lock = new Object();
    }

    protected void runOnUiThread(Runnable runnable) {
        mUIHandler.post(runnable);
    }
}
