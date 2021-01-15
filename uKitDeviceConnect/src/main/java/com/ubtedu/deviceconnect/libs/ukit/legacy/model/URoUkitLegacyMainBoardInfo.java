package com.ubtedu.deviceconnect.libs.ukit.legacy.model;

import com.ubtedu.deviceconnect.libs.base.model.URoMainBoardInfo;

import java.util.Arrays;
import java.util.Locale;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitLegacyMainBoardInfo extends URoMainBoardInfo {

    public URoUkitLegacyMainBoardInfo(byte[] bizData) {
        int offset = 0;
        setBoardVersionByBytes(Arrays.copyOfRange(bizData, offset, offset + 10));
        offset += 10;
        if(offset >= bizData.length) return;
        setBatteryByByte(bizData[offset]);
        offset += 1;
        if(offset >= bizData.length) return;
        steeringGear.setup(Arrays.copyOfRange(bizData, offset, offset + 16), 4, 4, 4, 4, 0);
        offset += 16;
        if(offset >= bizData.length) return;
        setFlashSize(bizData[offset]);
        offset += 1;
        if(offset >= bizData.length) return;
        infrared.setup(Arrays.copyOfRange(bizData, offset, offset + 7), 1, 1, 4, 1);
        offset += 7;
        if(offset >= bizData.length) return;
//        gyro.setup(Arrays.copyOfRange(bizData, offset, offset + 7), 1, 1, 4, 1);
        offset += 7;
        if(offset >= bizData.length) return;
        touch.setup(Arrays.copyOfRange(bizData, offset, offset + 7), 1, 1, 4, 1);
        offset += 7;
        if(offset >= bizData.length) return;
        lighting.setup(Arrays.copyOfRange(bizData, offset, offset + 7), 1, 1, 4, 1);
        offset += 7;
        if(offset >= bizData.length) return;
//        gravity.setup(Arrays.copyOfRange(bizData, offset, offset + 7), 1, 1, 4, 1);
        offset += 7;
        if(offset >= bizData.length) return;
        ultrasound.setup(Arrays.copyOfRange(bizData, offset, offset + 7), 1, 1, 4, 1);
        ultrasound.ignoreConflict();//超声传感器忽略版本不一致
        offset += 7;
        if(offset >= bizData.length) return;
//        led.setup(Arrays.copyOfRange(bizData, offset, offset + 7), 1, 1, 4, 1);
        offset += 7;
        if(offset >= bizData.length) return;
        speaker.setup(Arrays.copyOfRange(bizData, offset, offset + 7), 1, 1, 4, 1);
        offset += 7;
        if(offset >= bizData.length) return;
        envLight.setup(Arrays.copyOfRange(bizData, offset, offset + 7), 1, 1, 4, 1);
        envLight.ignoreConflict();//亮度传感器忽略版本不一致
        offset += 7;
        if(offset >= bizData.length) return;
//        airPressure.setup(Arrays.copyOfRange(bizData, offset, offset + 7), 1, 1, 4, 1);
        offset += 7;
        if(offset >= bizData.length) return;
        sound.setup(Arrays.copyOfRange(bizData, offset, offset + 7), 1, 1, 4, 1);
        sound.ignoreConflict();//声音传感器忽略版本不一致
        offset += 7;
        if(offset >= bizData.length) return;
        humiture.setup(Arrays.copyOfRange(bizData, offset, offset + 7), 1, 1, 4, 1);
        humiture.ignoreConflict();//温湿度传感器忽略版本不一致
        offset += 7;
        if(offset >= bizData.length) return;
        color.setup(Arrays.copyOfRange(bizData, offset, offset + 7), 1, 1, 4, 1);
        color.ignoreConflict();//颜色传感器忽略版本不一致
        offset += 7;
        if(offset >= bizData.length) return;
        motor.setup(Arrays.copyOfRange(bizData, offset, offset + 7), 1, 1, 4, 1);
        motor.ignoreConflict();//电机忽略版本不一致
        offset += 7;
        if(offset >= bizData.length) return;
//        rgbLed.setup(Arrays.copyOfRange(bizData, offset, offset + 7), 1, 1, 4, 1);
    }

    private void setBoardVersionByBytes(byte[] bytes) {
        boardVersion = new String(bytes);
    }

    private void setBatteryByByte(int bits) {
        batteryType = (bits & 0x80) != 0 ? BATTERY_TYPE_DRY_CELL : BATTERY_TYPE_LITHIUM;
        batteryLevel = Float.parseFloat(String.format(Locale.US,"%.2f", (bits & 0x7F) / 10.0f));
    }

    private void setFlashSize(byte b) {
        flashSize = Integer.parseInt(String.format(Locale.US,"%02x", b), 16);
    }

}
