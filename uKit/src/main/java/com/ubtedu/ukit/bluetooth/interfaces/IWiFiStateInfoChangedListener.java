package com.ubtedu.ukit.bluetooth.interfaces;

import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoWiFiStatusInfo;

public interface IWiFiStateInfoChangedListener {
    void onWiFiStateInfoChanged(URoWiFiStatusInfo wifiStatusInfo);
}
