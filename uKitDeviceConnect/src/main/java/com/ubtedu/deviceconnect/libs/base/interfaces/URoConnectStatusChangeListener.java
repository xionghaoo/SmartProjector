package com.ubtedu.deviceconnect.libs.base.interfaces;

import com.ubtedu.deviceconnect.libs.base.product.URoConnectStatus;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;

/**
 * @Author naOKi
 * @Date 2018/11/06
 **/
public interface URoConnectStatusChangeListener {
    void onConnectStatusChanged(URoProduct product, URoConnectStatus connectStatus);
}
