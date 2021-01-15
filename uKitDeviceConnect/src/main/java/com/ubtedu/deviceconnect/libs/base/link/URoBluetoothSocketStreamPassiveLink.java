package com.ubtedu.deviceconnect.libs.base.link;

import android.bluetooth.BluetoothSocket;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.model.URoLinkModel;

/**
 * @Author naOKi
 * @Date 2019/08/13
 **/
public class URoBluetoothSocketStreamPassiveLink extends URoIoStreamLink<BluetoothSocket> {

    public URoBluetoothSocketStreamPassiveLink(@NonNull URoLinkModel model, @NonNull URoBluetoothSocketStream source) throws Exception {
        super(model, source);
    }

    public URoBluetoothSocketStreamPassiveLink(@NonNull URoLinkModel model, @NonNull BluetoothSocket source) throws Exception {
        this(model, new URoBluetoothSocketStream(source));
    }

    @Override
    protected URoLinkReceiveMode setupLinkReceiveMode() {
        return URoLinkReceiveMode.PASSIVE;
    }

}
