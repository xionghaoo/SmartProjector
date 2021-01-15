package com.ubtedu.ukit.bluetooth.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Locale;

/**
 * @Author naOKi
 * @Date 2018/12/10
 **/
public class SensorValueData {

    public static final int INVALID_VALUE = -9999;

    @SerializedName("battery")
    private BatteryData[] battery = {new BatteryData()};

    @SerializedName("infrared")
    private ArrayList<CommonIdValueData> infrared = null;

    @SerializedName("touch")
    private ArrayList<CommonIdValueData> touch = null;

    @SerializedName("color")
    private ArrayList<CommonIdValueData> color = null;

    @SerializedName("ultrasound")
    private ArrayList<CommonIdValueData> ultrasound = null;

    @SerializedName("humiture")
    private ArrayList<HumitureIdValueData> humiture = null;

    @SerializedName("envLight")
    private ArrayList<CommonIdValueData> envLight = null;

    @SerializedName("sound")
    private ArrayList<CommonIdValueData> sound = null;

    public void setBatteryValue(boolean lowBattery) {
        battery[0].result = String.valueOf(lowBattery ? 1 : 0);
    }

    public void addInfraredValue(int id, int value) {
        if(infrared == null) {
            infrared = new ArrayList<>();
        }
        infrared.add(new CommonIdValueData(id, value));
    }

    public void addTouchValue(int id, int value) {
        if(touch == null) {
            touch = new ArrayList<>();
        }
        touch.add(new CommonIdValueData(id, value));
    }

    public void addColorValue(int id, int value) {
        if(this.color == null) {
            this.color = new ArrayList<>();
        }
        if(value == INVALID_VALUE) {
            String color = String.valueOf(INVALID_VALUE);
            this.color.add(new CommonIdValueData(id, color));
        } else {
            int r = (value >> 16) & 0xFF;
            int g = (value >> 8) & 0xFF;
            int b = value & 0xFF;
            String color = String.format(Locale.US,"#%02X%02X%02X", r, g, b);
            this.color.add(new CommonIdValueData(id, color));
        }
    }

    public void addUltrasoundValue(int id, int value) {
        if(ultrasound == null) {
            ultrasound = new ArrayList<>();
        }
        ultrasound.add(new CommonIdValueData(id, value));
    }

    public void addEnvLightValue(int id, int value) {
        if(envLight == null) {
            envLight = new ArrayList<>();
        }
        envLight.add(new CommonIdValueData(id, value));
    }

    public void addSoundValue(int id, int value) {
        if(sound == null) {
            sound = new ArrayList<>();
        }
        sound.add(new CommonIdValueData(id, value));
    }

    public void addHumitureValue(int id, int humiture, int temperature) {
        if(this.humiture == null) {
            this.humiture = new ArrayList<>();
        }
        this.humiture.add(new HumitureIdValueData(id, humiture, temperature));
    }

    public class BatteryData {
        @SerializedName("result")
        public String result;
    }

    public class CommonIdValueData {
        @SerializedName("id")
        public String id;
        @SerializedName("result")
        public String result;
        public CommonIdValueData(int id, int result) {
            this.id = String.valueOf(id);
            this.result = String.valueOf(result);
        }
        public CommonIdValueData(int id, String result) {
            this.id = String.valueOf(id);
            this.result = result;
        }
    }

    public class HumitureIdValueData {
        @SerializedName("id")
        public String id;
        @SerializedName("humiture")
        public String humiture;
        @SerializedName("temperature")
        public String temperature;
        public HumitureIdValueData(int id, int humiture, int temperature) {
            this.id = String.valueOf(id);
            this.humiture = String.valueOf(humiture);
            this.temperature = String.valueOf(temperature);
        }
    }

}
