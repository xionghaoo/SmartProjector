package com.ubtedu.deviceconnect.libs.ukit.connector;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.connector.URoBluetoothDevice;
import com.ubtedu.deviceconnect.libs.base.model.URoConnectInfo;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public class URoUkitBluetoothInfo extends URoConnectInfo {
    public final int rssi;
    public final URoBluetoothDevice device;

    public URoUkitBluetoothInfo(int rssi, @NonNull BluetoothDevice device) {
        this.rssi = rssi;
        this.device = new URoBluetoothDevice(device, rssi);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof URoUkitBluetoothInfo)) return false;

        URoUkitBluetoothInfo that = (URoUkitBluetoothInfo) o;

        return device.equals(that.device);
    }

    @Override
    public int hashCode() {
        return device.hashCode();
    }

}
