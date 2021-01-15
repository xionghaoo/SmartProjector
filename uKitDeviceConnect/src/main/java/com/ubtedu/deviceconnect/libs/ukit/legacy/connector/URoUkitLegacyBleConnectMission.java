package com.ubtedu.deviceconnect.libs.ukit.legacy.connector;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.connector.URoBluetoothDevice;
import com.ubtedu.deviceconnect.libs.base.connector.URoConnectMission;
import com.ubtedu.deviceconnect.libs.base.link.URoBleMgr;
import com.ubtedu.deviceconnect.libs.ukit.connector.URoUkitBluetoothInfo;

import java.net.ConnectException;

/**
 * @Author naOKi
 * @Date 2019/08/15
 **/
public class URoUkitLegacyBleConnectMission extends URoConnectMission<URoUkitBluetoothInfo, URoUkitLegacyBleMgrDelegate> {

    private final Object lock = new Object();
    private Boolean hasError = null;

    public URoUkitLegacyBleConnectMission(URoUkitBluetoothInfo connectItem) {
        super(connectItem);
    }

    public URoUkitLegacyBleConnectMission(URoUkitBluetoothInfo connectItem, @NonNull URoCompletionCallback<URoUkitLegacyBleMgrDelegate> callback) {
        super(connectItem, callback);
    }

    @Override
    protected URoUkitLegacyBleMgrDelegate connect(URoUkitBluetoothInfo connectItem) throws Exception {
        URoBluetoothDevice device = connectItem.device;
        URoUkitLegacyBleMgrDelegate delegate = new URoUkitLegacyBleMgrDelegate();
        URoCompletionCallback<Void> initCallback = new URoCompletionCallback<Void>() {
            @Override
            public void onComplete(URoCompletionResult<Void> result) {
                hasError = Boolean.valueOf(!result.isSuccess());
                synchronized (lock) {
                    lock.notify();
                }
            }
        };
        URoBleMgr bleMgr = new URoBleMgr<>(delegate);
//        bleMgr.refresh();
//        bleMgr.close();
        bleMgr.setInitCallback(initCallback);
        bleMgr.connect(device.getBluetoothDevice())
                .fail((dev, st) -> {
                    hasError = Boolean.TRUE;
                    synchronized (lock) {
                        lock.notify();
                    }
                })
                .invalid(() -> {
                    hasError = Boolean.TRUE;
                    synchronized (lock) {
                        lock.notify();
                    }
                })
                .useAutoConnect(false)
                .timeout(10000)
                .retry(3, 1000)
                .enqueue();
        if(hasError == null) {
            synchronized (lock) {
                lock.wait();
            }
        }
        if(hasError) {
            delegate.release();
            throw new ConnectException();
        }
        return delegate;
    }

    @Override
    protected void disconnect(URoUkitLegacyBleMgrDelegate delegate) {
        delegate.release();
    }

}
