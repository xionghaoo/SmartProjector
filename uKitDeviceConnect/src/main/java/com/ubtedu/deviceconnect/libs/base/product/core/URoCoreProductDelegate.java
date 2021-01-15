package com.ubtedu.deviceconnect.libs.base.product.core;

import com.ubtedu.deviceconnect.libs.base.product.URoConnectStatus;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public interface URoCoreProductDelegate {
    void onConnectivityChange(URoProduct product, URoConnectStatus status);
}
