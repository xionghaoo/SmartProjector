package com.ubtedu.deviceconnect.libs.base.model;

/**
 * @Author naOKi
 * @Date 2019/09/11
 **/
public enum URoComponentType {
    /**
     * 舵机
     */
    SERVOS("舵机"),
    /**
     * 红外
     */
    INFRAREDSENSOR("红外传感器"),
    /**
     * 触碰传感器
     */
    TOUCHSENSOR("触碰传感器"),
    /**
     * 超声
     */
    ULTRASOUNDSENSOR("超声波传感器"),
    /**
     * 眼灯
     */
    LED("LED灯"),
    /**
     * 音箱
     */
    SPEAKER("蓝牙音箱"),
    /**
     * 颜色传感器
     */
    COLORSENSOR("颜色传感器"),
    /**
     * 温湿度传感器
     */
    ENVIRONMENTSENSOR("温湿度传感器"),
    /**
     * 亮度传感器
     */
    BRIGHTNESSSENSOR("亮度传感器"),
    /**
     * 声音传感器
     */
    SOUNDSENSOR("声音传感器"),
    /**
     * 电机
     */
    MOTOR("电机"),
    /**
     * 吸盘
     */
    SUCKER("吸盘传感器"),
    /**
     * 灯盒
     */
    LED_BELT("灯盒传感器"),
    /**
     * 灯带
     */
    LED_STRIP("灯带"),
    /**
     * 显示屏
     */
    LCD("显示屏"),
    /**
     * 视觉模块
     */
    VISION("视觉模块");

    private String displayName;

    public String getDisplayName() {
        return displayName;
    }

    URoComponentType(String displayName) {
        this.displayName = displayName;
    }

}
