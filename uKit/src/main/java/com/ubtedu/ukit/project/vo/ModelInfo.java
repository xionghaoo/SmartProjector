/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.vo;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.ubtedu.deviceconnect.libs.base.model.URoLEDStripInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoMainBoardInfo;
import com.ubtedu.ukit.common.vo.SerializablePOJO;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @Author qinicy
 * @Date 2018/11/5
 **/
public class ModelInfo extends ProjectFile {
    @SerializedName("steeringGear")
    public ArrayList<Integer> steeringGear = new ArrayList<>();
    @SerializedName("steeringGearAngular")
    public ArrayList<Integer> steeringGearAngular = new ArrayList<>();
    @SerializedName("steeringGearWheel")
    public ArrayList<Integer> steeringGearWheel = new ArrayList<>();
    @SerializedName("motor")
    public ArrayList<Integer> motor = new ArrayList<>();
    @SerializedName("infrared")
    public ArrayList<Integer> infrared = new ArrayList<>();
    @SerializedName("ultrasound")
    public ArrayList<Integer> ultrasound = new ArrayList<>();
    @SerializedName("led")
    public ArrayList<Integer> led = new ArrayList<>();
    @SerializedName("touch")
    public ArrayList<Integer> touch = new ArrayList<>();
    @SerializedName("humidity")
    public ArrayList<Integer> humidity = new ArrayList<>();
    @SerializedName("brightness")
    public ArrayList<Integer> brightness = new ArrayList<>();
    @SerializedName("decibel")
    public ArrayList<Integer> decibel = new ArrayList<>();
    @SerializedName("color")
    public ArrayList<Integer> color = new ArrayList<>();
    @SerializedName("speaker")
    public ArrayList<Integer> speaker = new ArrayList<>();
    @SerializedName("lightBox")
    public ArrayList<LightBoxInfo> lightBox = new ArrayList<>();

    public static class LightStripInfo extends SerializablePOJO{
        @SerializedName("port")
        public int port;
        @SerializedName("ledNumber")
        public int ledNumber;
        public LightStripInfo(int port, int ledNumber) {
            this.port = port;
            this.ledNumber = ledNumber;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof LightStripInfo)) {
                return false;
            }
            return ((LightStripInfo) obj).port == this.port && ((LightStripInfo) obj).ledNumber==this.ledNumber;
        }

        @Override
        public int hashCode() {
            return Objects.hash(port);
        }
    }

    public static class LightBoxInfo extends SerializablePOJO {
        public LightBoxInfo(int id, ArrayList<LightStripInfo> lightStrip) {
            this.id = id;
            this.lightStrip = new ArrayList<>(lightStrip);
        }

        @SerializedName("id")
        public int id;
        @SerializedName("lightStrip")
        public ArrayList<LightStripInfo> lightStrip;

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof LightBoxInfo)) {
                return false;
            }
            return ((LightBoxInfo) obj).id == this.id && isSameList(this.lightStrip, ((LightBoxInfo) obj).lightStrip);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    public static byte getAvailableIdByte(ArrayList<Integer> availableIds) {
        byte result = 0;
        for (Integer availableId : availableIds) {
            if (availableId < 1 || availableId > 8) {
                continue;
            }
            result |= 1 << (availableId - 1);
        }
        return result;
    }

    public void addSteeringGearAngular(Integer id) {
        if (id < 1 || id > 32) {
            return;
        }
        if (!steeringGear.contains(id)) {
            steeringGear.add(id);
        }
        if (!steeringGearAngular.contains(id)) {
            steeringGearAngular.add(id);
        }
        if (steeringGearWheel.contains(id)) {
            steeringGearWheel.remove(id);
        }
    }

    public void addSteeringGearWheel(Integer id) {
        if (id < 1 || id > 32) {
            return;
        }
        if (!steeringGear.contains(id)) {
            steeringGear.add(id);
        }
        if (steeringGearAngular.contains(id)) {
            steeringGearAngular.remove(id);
        }
        if (!steeringGearWheel.contains(id)) {
            steeringGearWheel.add(id);
        }
    }

    public static ModelInfo newInstance(URoMainBoardInfo bia) {
        ModelInfo info = new ModelInfo();
        if (bia != null) {
            info.steeringGear.addAll(bia.steeringGear.getAvailableIds());
            info.steeringGearAngular.addAll(bia.steeringGear.getAvailableIds());
            info.motor.addAll(bia.motor.getAvailableIds());
            info.infrared.addAll(bia.infrared.getAvailableIds());
            info.ultrasound.addAll(bia.ultrasound.getAvailableIds());
            info.led.addAll(bia.lighting.getAvailableIds());
            info.touch.addAll(bia.touch.getAvailableIds());
            info.humidity.addAll(bia.humiture.getAvailableIds());
            info.brightness.addAll(bia.envLight.getAvailableIds());
            info.decibel.addAll(bia.sound.getAvailableIds());
            info.color.addAll(bia.color.getAvailableIds());
            info.speaker.addAll(bia.speaker.getAvailableIds());
            info.lightBox.addAll(getLEDStrip(bia));
        }
        return info;
    }

    public static ArrayList<LightBoxInfo> getLEDStrip(URoMainBoardInfo mainBoardInfo) {
        ArrayList<LightBoxInfo> lightBox = new ArrayList();
        for (int i = 0; i < mainBoardInfo.ledBelt.getAvailableIds().size(); i++) {
            int boxId = mainBoardInfo.ledBelt.getAvailableIds().get(i);
            ArrayList<LightStripInfo> stripList = new ArrayList<>();
            ArrayList<URoLEDStripInfo> allStripList = mainBoardInfo.ledBelt.getSubComponent(boxId);
            for (int j = 0; j < allStripList.size(); j++) {
                URoLEDStripInfo ledStripInfo=allStripList.get(j);
                if (ledStripInfo != null) {
                    if (ledStripInfo.getLedNumber() > 0) {
                        stripList.add(new LightStripInfo(ledStripInfo.getPort(), ledStripInfo.getLedNumber()));
                    }
                }
            }
            lightBox.add(new LightBoxInfo(boxId, stripList));
        }
        return lightBox;
    }

    public static ModelInfo newInstance(ModelInfo pi) {
        ModelInfo info = new ModelInfo();
        if (pi != null) {
            info.steeringGear.addAll(pi.steeringGear);
            info.steeringGearAngular.addAll(pi.steeringGearAngular);
            info.steeringGearWheel.addAll(pi.steeringGearWheel);
            info.motor.addAll(pi.motor);
            info.infrared.addAll(pi.infrared);
            info.ultrasound.addAll(pi.ultrasound);
            info.led.addAll(pi.led);
            info.touch.addAll(pi.touch);
            info.humidity.addAll(pi.humidity);
            info.brightness.addAll(pi.brightness);
            info.decibel.addAll(pi.decibel);
            info.color.addAll(pi.color);
            info.speaker.addAll(pi.speaker);
            info.lightBox.addAll(pi.lightBox);
        }
        return info;
    }

    public static void mergeModelInfo(ModelInfo source, ModelInfo target) {
        if (source == null || target == null) {
            return;
        }
        target.steeringGear.clear();
        target.steeringGearAngular.clear();
        target.steeringGearWheel.clear();
        target.motor.clear();
        target.infrared.clear();
        target.ultrasound.clear();
        target.led.clear();
        target.touch.clear();
        target.humidity.clear();
        target.brightness.clear();
        target.decibel.clear();
        target.color.clear();
        target.speaker.clear();
        target.lightBox.clear();
        target.steeringGear.addAll(source.steeringGear);
        target.steeringGearAngular.addAll(source.steeringGearAngular);
        target.steeringGearWheel.addAll(source.steeringGearWheel);
        target.motor.addAll(source.motor);
        target.infrared.addAll(source.infrared);
        target.ultrasound.addAll(source.ultrasound);
        target.led.addAll(source.led);
        target.touch.addAll(source.touch);
        target.humidity.addAll(source.humidity);
        target.brightness.addAll(source.brightness);
        target.decibel.addAll(source.decibel);
        target.color.addAll(source.color);
        target.speaker.addAll(source.speaker);
        target.lightBox.addAll(source.lightBox);
    }

    public boolean isSameContent(ModelInfo modelInfo) {
        if (null == modelInfo) return false;
        if (this == modelInfo) return true;
        if (!isSameList(steeringGear, modelInfo.steeringGear)) return false;
        if (!isSameList(steeringGearAngular, modelInfo.steeringGearAngular)) return false;
        if (!isSameList(steeringGearWheel, modelInfo.steeringGearWheel)) return false;
        if (!isSameList(motor, modelInfo.motor)) return false;
        if (!isSameList(infrared, modelInfo.infrared)) return false;
        if (!isSameList(ultrasound, modelInfo.ultrasound)) return false;
        if (!isSameList(led, modelInfo.led)) return false;
        if (!isSameList(touch, modelInfo.touch)) return false;
        if (!isSameList(humidity, modelInfo.humidity)) return false;
        if (!isSameList(brightness, modelInfo.brightness)) return false;
        if (!isSameList(decibel, modelInfo.decibel)) return false;
        if (!isSameList(color, modelInfo.color)) return false;
        if (!isSameList(lightBox, modelInfo.lightBox)) return false;
        return isSameList(speaker, modelInfo.speaker);
    }

    private static <T> boolean isSameList(ArrayList<T> list1, ArrayList<T> list2) {
        if ((list1 == null || list1.isEmpty()) && (list2 == null || list2.isEmpty())) return true;
        if (list1 == null || list2 == null) return false;
        if (list1 == list2) return true;
        if (list1.size() != list2.size()) return false;
        ArrayList<T> tmp = new ArrayList<>(list1);
        tmp.removeAll(list2);
        return tmp.isEmpty();
    }

}
