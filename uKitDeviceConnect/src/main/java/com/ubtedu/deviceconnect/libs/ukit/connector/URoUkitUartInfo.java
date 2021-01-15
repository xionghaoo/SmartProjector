package com.ubtedu.deviceconnect.libs.ukit.connector;

import android.hardware.usb.UsbDevice;

import com.ubtedu.deviceconnect.libs.base.model.URoConnectInfo;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public class URoUkitUartInfo extends URoConnectInfo {
    public final UsbDevice device;

    public URoUkitUartInfo(UsbDevice device) {
        this.device = device;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof URoUkitUartInfo)) return false;

        URoUkitUartInfo that = (URoUkitUartInfo) o;

        return device.equals(that.device);
    }

    @Override
    public int hashCode() {
        return device.hashCode();
    }

}
