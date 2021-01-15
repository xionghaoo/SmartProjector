/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.ota;

import androidx.annotation.Keep;

/**
 * @Author qinicy
 * @Date 2018/12/18
 **/
@Keep
public enum OtaComponent {
    Blockly("Blockly"),
    Mainboard("Mainboard"),
    Mainboard2("Mainboard2"),
    MainboardVB("MainboardVB"),
    Servo("Servo"),
    Infrared("Infrared", 101),
    Touch("Touch", 102),
    Gyroscope("Gyroscope", 103),
    Light("Light", 104),
    Speaker("Speaker", 108),
    LEDBox("LEDBox");

    private String name;
    private int sensorType = -1;

    public String getName() {
        return name;
    }

    public int getSensorType() {
        return sensorType;
    }

    OtaComponent(String name) {
        this.name = name;
    }

    OtaComponent(String name, int sensorType) {
        this.name = name;
        this.sensorType = sensorType;
    }
}
