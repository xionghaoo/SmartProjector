package com.ubtedu.deviceconnect.libs.ukit.smart.connector;

import android.bluetooth.BluetoothAdapter;
import android.os.Build;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.connector.URoBluetoothDevice;
import com.ubtedu.deviceconnect.libs.base.connector.URoConnectMission;
import com.ubtedu.deviceconnect.libs.base.link.URoBleMgr;
import com.ubtedu.deviceconnect.libs.ukit.connector.URoUkitBluetoothInfo;

import java.net.ConnectException;

import no.nordicsemi.android.ble.PhyRequest;

/**
 * @Author naOKi
 * @Date 2019/08/15
 **/
public class URoUkitSmartBleConnectMission extends URoConnectMission<URoUkitBluetoothInfo, URoUkitSmartBleMgrDelegate> {

    private final Object lock = new Object();
    private Boolean hasError = null;

    public URoUkitSmartBleConnectMission(URoUkitBluetoothInfo connectItem) {
        super(connectItem);
    }

    public URoUkitSmartBleConnectMission(URoUkitBluetoothInfo connectItem, @NonNull URoCompletionCallback<URoUkitSmartBleMgrDelegate> callback) {
        super(connectItem, callback);
    }

    @Override
    protected URoUkitSmartBleMgrDelegate connect(URoUkitBluetoothInfo connectItem) throws Exception {
        URoBluetoothDevice device = connectItem.device;
        URoUkitSmartBleMgrDelegate delegate = new URoUkitSmartBleMgrDelegate();
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
//        int phy = PhyRequest.PHY_LE_1M_MASK;
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
//            if(adapter != null && adapter.isLe2MPhySupported()) {
//                phy = PhyRequest.PHY_LE_2M_MASK;
//            } else if(adapter != null && adapter.isLeCodedPhySupported()) {
//                phy = PhyRequest.PHY_LE_CODED_MASK;
//            }
//        }
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
//                .usePreferredPhy(phy)
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
    protected void disconnect(URoUkitSmartBleMgrDelegate delegate) {
        delegate.release();
    }

}
