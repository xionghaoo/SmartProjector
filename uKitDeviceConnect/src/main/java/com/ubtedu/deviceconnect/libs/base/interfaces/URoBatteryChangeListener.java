package com.ubtedu.deviceconnect.libs.base.interfaces;

import com.ubtedu.deviceconnect.libs.base.model.URoBatteryInfo;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;

/**
 * @Author naOKi
 * @Date 2018/11/06
 **/
public interface URoBatteryChangeListener {
    void onBatteryInfoUpdated(URoProduct product, URoBatteryInfo batteryInfo);
}
