package com.ubtedu.deviceconnect.libs.base.link;

import androidx.annotation.NonNull;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.ubtedu.deviceconnect.libs.base.model.URoLinkModel;
import com.ubtedu.deviceconnect.libs.base.product.URoConnectStatus;

/**
 * @Author naOKi
 * @Date 2019/08/13
 **/
public class URoUsbDeviceStreamLink extends URoIoStreamLink<UsbSerialPort> {

    public URoUsbDeviceStreamLink(@NonNull URoLinkModel model, @NonNull URoUsbDeviceStream source) throws Exception {
        super(model, source);
        source.setConnectChangedCallback(new URoBluetoothGattConnectChangedCallback() {
            @Override
            public void onConnectStatusChanged(boolean isConnected) {
                if(isConnected) {
                    onConnectivityChange(URoUsbDeviceStreamLink.this, URoConnectStatus.CONNECTED);
                } else {
                    onConnectivityChange(URoUsbDeviceStreamLink.this, URoConnectStatus.DISCONNECTED);
                    disconnect();
                }
            }
        });
    }

}
