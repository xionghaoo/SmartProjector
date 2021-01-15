package com.ubtedu.deviceconnect.libs.base.connector;

import com.ubtedu.deviceconnect.libs.base.model.URoConnectInfo;

/**
 * @Author naOKi
 * @Date 2018/11/06
 **/
public interface URoDiscoveryCallback<C extends URoConnectInfo> {
    void onDeviceFound(C connectItem);
    void onDiscoveryFinish();
}
