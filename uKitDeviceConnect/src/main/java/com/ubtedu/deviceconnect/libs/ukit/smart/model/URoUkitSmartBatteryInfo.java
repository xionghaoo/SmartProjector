package com.ubtedu.deviceconnect.libs.ukit.smart.model;

import com.ubtedu.deviceconnect.libs.base.model.URoBatteryInfo;
import com.ubtedu.deviceconnect.libs.ukit.model.URoUkitBatteryInfo;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitSmartBatteryInfo extends URoUkitBatteryInfo {
    public static final float LOW_BATTERY_THRESHOLD = 7.335f;
    public static final float DISCONNECT_BATTERY_THRESHOLD = 7.3f;

    public URoUkitSmartBatteryInfo(URoBatteryInfo batteryInfo) {
        if(batteryInfo != null) {
            setCharging(batteryInfo.isCharging());
            setFullCharge(batteryInfo.isFullCharge());
            setVoltage(batteryInfo.getVoltage());
            setBatteryRemaining(batteryInfo.getBatteryRemaining());
        }
    }

    public void updateBatteryRemaining(int batteryRemaining) {
        setBatteryRemaining(batteryRemaining);
    }

    public void updateVoltage(float voltage) {
        setVoltage(voltage);
    }

    public void updateCharging(boolean charging) {
        setCharging(charging);
    }

    public void updateFullCharge(boolean fullCharge) {
        setFullCharge(fullCharge);
    }

    @Override
    public float getLowBatteryThreshold() {
        return LOW_BATTERY_THRESHOLD;
    }

    @Override
    public float getDisconnectBatteryThreshold() {
        return DISCONNECT_BATTERY_THRESHOLD;
    }
}
