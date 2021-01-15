package com.ubtedu.deviceconnect.libs.ukit.smart.model;

import android.hardware.usb.UsbDevice;

import com.ubtedu.deviceconnect.libs.base.link.URoLinkType;
import com.ubtedu.deviceconnect.libs.base.model.URoLinkModel;

/**
 * @Author naOKi
 * @Date 2019/08/13
 **/
public class URoUkitSmartUartLinkModel extends URoLinkModel {

    public final UsbDevice device;

    public URoUkitSmartUartLinkModel(URoLinkType linkType, String linkName, UsbDevice device) {
        super(linkType, linkName, device.getSerialNumber());
        this.device = device;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof URoUkitSmartUartLinkModel)) return false;
        if (!super.equals(o)) return false;

        URoUkitSmartUartLinkModel that = (URoUkitSmartUartLinkModel) o;

        if (device != null ? !device.equals(that.device) : that.device != null) return false;
        if (linkType != that.linkType) return false;
        return linkName != null ? linkName.equals(that.linkName) : that.linkName == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (device != null ? device.hashCode() : 0);
        result = 31 * result + linkType.hashCode();
        result = 31 * result + (linkName != null ? linkName.hashCode() : 0);
        return result;
    }

}
