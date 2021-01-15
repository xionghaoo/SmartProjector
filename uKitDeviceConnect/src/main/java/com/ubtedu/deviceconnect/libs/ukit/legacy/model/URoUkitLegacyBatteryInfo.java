package com.ubtedu.deviceconnect.libs.ukit.legacy.model;

import com.ubtedu.deviceconnect.libs.ukit.model.URoUkitBatteryInfo;

import java.util.Locale;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitLegacyBatteryInfo extends URoUkitBatteryInfo {
    public static final float LOW_BATTERY_THRESHOLD = 7.2f;
    public static final float DISCONNECT_BATTERY_THRESHOLD = 7.0f;
    public URoUkitLegacyBatteryInfo(byte[] bizData) {
        setCharging(bizData[0] == 0x01);
        setFullCharge(bizData[1] == 0x01);
        setVoltage(Float.parseFloat(String.format(Locale.US, "%.2f", Integer.parseInt(String.format(Locale.ENGLISH, "%02x", bizData[2]), 16) / 10.0f)));
        setBatteryRemaining(Integer.parseInt(String.format(Locale.US, "%02x", bizData[3]), 16));
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
