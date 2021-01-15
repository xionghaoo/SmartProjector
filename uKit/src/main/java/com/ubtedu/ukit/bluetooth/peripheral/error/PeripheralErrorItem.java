package com.ubtedu.ukit.bluetooth.peripheral.error;

import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.UKitApplication;

import java.io.Serializable;

/**
 * @Author naOKi
 * @Date 2018-12-12
 **/
public class PeripheralErrorItem implements Serializable {
    private int id;
    private URoComponentType type;
    private boolean isSteeringGear;

    public static PeripheralErrorItem newSteeringGearError(int id) {
        return new PeripheralErrorItem(id, URoComponentType.SERVOS, true);
    }

    public static PeripheralErrorItem newSensorError(int id, URoComponentType type) {
        return new PeripheralErrorItem(id, type, false);
    }

    private PeripheralErrorItem(int id, URoComponentType type, boolean isSteeringGear) {
        this.id = id;
        this.type = type;
        this.isSteeringGear = isSteeringGear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PeripheralErrorItem)) return false;

        PeripheralErrorItem that = (PeripheralErrorItem) o;

        if (getId() != that.getId()) return false;
        if (isSteeringGear() != that.isSteeringGear()) return false;
        return getType() == that.getType();
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        result = 31 * result + (isSteeringGear() ? 1 : 0);
        return result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public URoComponentType getType() {
        return type;
    }

    public void setType(URoComponentType type) {
        this.type = type;
    }

    public boolean isSteeringGear() {
        return isSteeringGear;
    }

    public void setSteeringGear(boolean steeringGear) {
        isSteeringGear = steeringGear;
    }

    @Override
    public String toString() {
        int resId = -1;
        if (isSteeringGear) {
            resId = R.string.bluetooth_peripheral_steering_gear;
        } else {
            switch (type) {
                case INFRAREDSENSOR:
                    resId = R.string.bluetooth_peripheral_infrared;
                    break;
                case TOUCHSENSOR:
                    resId = R.string.bluetooth_peripheral_touch;
                    break;
                case LED:
                    resId = R.string.bluetooth_peripheral_lighting;
                    break;
                case ULTRASOUNDSENSOR:
                    resId = R.string.bluetooth_peripheral_ultrasound;
                    break;
                case SPEAKER:
                    resId = R.string.bluetooth_peripheral_speaker;
                    break;
                case COLORSENSOR:
                    resId = R.string.bluetooth_peripheral_color;
                    break;
                case ENVIRONMENTSENSOR:
                    resId = R.string.bluetooth_peripheral_humiture;
                    break;
                case BRIGHTNESSSENSOR:
                    resId = R.string.bluetooth_peripheral_env_light;
                    break;
                case SOUNDSENSOR:
                    resId = R.string.bluetooth_peripheral_sound;
                    break;
                case MOTOR:
                    resId = R.string.bluetooth_peripheral_motor;
                    break;
                case LED_BELT:
                    resId = R.string.bluetooth_peripheral_led_box;
                    break;
            }
        }
        if (resId == -1) {
            return "";
        }
        String peripheralName = UKitApplication.getInstance().getString(resId);
        return String.format("%sID-%s", peripheralName, String.valueOf(id));
    }
}
