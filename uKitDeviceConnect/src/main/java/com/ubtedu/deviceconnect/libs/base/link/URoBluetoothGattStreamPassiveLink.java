package com.ubtedu.deviceconnect.libs.base.link;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.model.URoLinkModel;
import com.ubtedu.deviceconnect.libs.base.product.URoConnectStatus;

/**
 * @Author naOKi
 * @Date 2019/08/13
 **/
public class URoBluetoothGattStreamPassiveLink<D extends URoBleMgrDelegate> extends URoIoStreamLink<D> {

    public URoBluetoothGattStreamPassiveLink(@NonNull URoLinkModel model, @NonNull URoBluetoothGattStreamPassive<D> source) throws Exception {
        super(model, source);
        source.getSource().setDataReceivedCallback(new URoBluetoothGattDataReceivedCallback() {
            @Override
            public void onReceiveData(byte[] data) {
                URoBluetoothGattStreamPassiveLink.this.onReceiveData(URoBluetoothGattStreamPassiveLink.this, data);
            }
        });
        source.getSource().setConnectChangedCallback(new URoBluetoothGattConnectChangedCallback() {
            @Override
            public void onConnectStatusChanged(boolean isConnected) {
                if(isConnected) {
                    onConnectivityChange(URoBluetoothGattStreamPassiveLink.this, URoConnectStatus.CONNECTED);
                } else {
                    onConnectivityChange(URoBluetoothGattStreamPassiveLink.this, URoConnectStatus.DISCONNECTED);
                    disconnect();
                }
            }
        });
    }

    @Override
    protected URoLinkReceiveMode setupLinkReceiveMode() {
        return URoLinkReceiveMode.PASSIVE;
    }

}
