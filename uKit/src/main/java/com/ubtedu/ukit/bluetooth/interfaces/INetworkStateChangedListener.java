package com.ubtedu.ukit.bluetooth.interfaces;

import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoNetworkState;

public interface INetworkStateChangedListener {
    void onNetworkStateChanged(URoNetworkState networkState);
}
