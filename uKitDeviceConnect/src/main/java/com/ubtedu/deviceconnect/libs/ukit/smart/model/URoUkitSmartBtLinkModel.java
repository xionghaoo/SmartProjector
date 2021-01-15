package com.ubtedu.deviceconnect.libs.ukit.smart.model;

import com.ubtedu.deviceconnect.libs.base.connector.URoBluetoothDevice;
import com.ubtedu.deviceconnect.libs.base.link.URoLinkType;
import com.ubtedu.deviceconnect.libs.base.model.URoLinkModel;

/**
 * @Author naOKi
 * @Date 2019/08/13
 **/
public class URoUkitSmartBtLinkModel extends URoLinkModel {

    public final int rssi;
    public final URoBluetoothDevice device;

    public URoUkitSmartBtLinkModel(URoLinkType linkType, String linkName, int rssi, URoBluetoothDevice device) {
        super(linkType, linkName, device.getAddress());
        this.rssi = rssi;
        this.device = device;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof URoUkitSmartBtLinkModel)) return false;
        if (!super.equals(o)) return false;

        URoUkitSmartBtLinkModel that = (URoUkitSmartBtLinkModel) o;

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
