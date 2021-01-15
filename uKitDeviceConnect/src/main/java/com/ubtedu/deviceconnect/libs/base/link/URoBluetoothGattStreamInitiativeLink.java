package com.ubtedu.deviceconnect.libs.base.link;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.model.URoLinkModel;
import com.ubtedu.deviceconnect.libs.base.product.URoConnectStatus;

/**
 * @Author naOKi
 * @Date 2019/08/13
 **/
public class URoBluetoothGattStreamInitiativeLink<D extends URoBleMgrDelegate> extends URoIoStreamLink<D> {

    public URoBluetoothGattStreamInitiativeLink(@NonNull URoLinkModel model, @NonNull URoBluetoothGattStreamInitiative<D> source) throws Exception {
        super(model, source);
        source.getSource().setConnectChangedCallback(new URoBluetoothGattConnectChangedCallback() {
            @Override
            public void onConnectStatusChanged(boolean isConnected) {
                if(isConnected) {
                    onConnectivityChange(URoBluetoothGattStreamInitiativeLink.this, URoConnectStatus.CONNECTED);
                } else {
                    onConnectivityChange(URoBluetoothGattStreamInitiativeLink.this, URoConnectStatus.DISCONNECTED);
                    disconnect();
                }
            }
        });
    }

}
