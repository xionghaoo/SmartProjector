package com.ubtedu.deviceconnect.libs.base.link;

import com.ubtedu.deviceconnect.libs.base.product.URoConnectStatus;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public interface URoLinkDelegate {

    void onConnectivityChange(URoLink link, URoConnectStatus status);
    void onReceiveData(URoLink link, byte[] data);

}
