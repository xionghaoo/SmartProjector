package com.ubtedu.deviceconnect.libs.base.model;

/**
 * @Author naOKi
 * @Date 2019/09/09
 **/
public class URoHumitureValue {

    private final int humidity;
    private final float temperature;

    public URoHumitureValue(int humidity, float temperature) {
        this.humidity = humidity;
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public float getTemperature() {
        return temperature;
    }

}
