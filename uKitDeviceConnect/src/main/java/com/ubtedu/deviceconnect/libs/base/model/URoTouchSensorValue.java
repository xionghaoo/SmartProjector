package com.ubtedu.deviceconnect.libs.base.model;

/**
 * @Author naOKi
 * @Date 2019/09/09
 **/
public class URoTouchSensorValue {

    public enum URoTouchSensorEvent {
        NONE,
        SINGLE_CLICK,
        DOUBLE_CLICK,
        LONG_PRESSED;
    }

    private final URoTouchSensorEvent status;

    public URoTouchSensorValue(int status) {
        if(status >= 0 && status < URoTouchSensorEvent.values().length) {
            this.status = URoTouchSensorEvent.values()[status];
        } else {
            this.status = URoTouchSensorEvent.NONE;
        }
    }

    public URoTouchSensorValue(URoTouchSensorEvent status) {
        this.status = status;
    }

    public URoTouchSensorEvent getStatus() {
        return status;
    }

    public boolean isSingleClick() {
        return URoTouchSensorEvent.SINGLE_CLICK.equals(status);
    }

    public boolean isDoubleClick() {
        return URoTouchSensorEvent.DOUBLE_CLICK.equals(status);
    }

    public boolean isLongPressed() {
        return URoTouchSensorEvent.LONG_PRESSED.equals(status);
    }

}
