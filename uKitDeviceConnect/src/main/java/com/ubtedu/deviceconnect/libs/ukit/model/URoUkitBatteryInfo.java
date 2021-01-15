package com.ubtedu.deviceconnect.libs.ukit.model;

import com.ubtedu.deviceconnect.libs.base.model.URoBatteryInfo;

/**
 * @Author naOKi
 * @Date 2018/11/06
 **/
public abstract class URoUkitBatteryInfo extends URoBatteryInfo {
    @Override
    public boolean isLowBattery() {
        return !isCharging() && getVoltage() < getLowBatteryThreshold();
    }

    @Override
    public boolean isEmptyBattery() {
        return !isCharging() && getVoltage() < getDisconnectBatteryThreshold();
    }

    public abstract float getLowBatteryThreshold();
    public abstract float getDisconnectBatteryThreshold();

}
