package com.ubtedu.deviceconnect.libs.base.model;

import java.util.ArrayList;

/**
 * @Author naOKi
 * @Date 2019/10/31
 **/
public class URoSubscribeSensorInfo {

    private URoComponentID id = null;
    private URoSubscribeSensorMode mode = null;
    private ArrayList<Integer> values = new ArrayList<>();

    public URoSubscribeSensorInfo setId(URoComponentID id) {
        this.id = id;
        return this;
    }

    public URoSubscribeSensorInfo setMode(URoSubscribeSensorMode mode) {
        this.mode = mode;
        return this;
    }

    public URoSubscribeSensorInfo addValue(int... values) {
        for(int value : values) {
            this.values.add(value);
        }
        return this;
    }

    public boolean isValid() {
        if(id == null || mode == null) {
            return false;
        }
        if(URoComponentType.SERVOS.equals(id.getComponentType())
                || URoComponentType.MOTOR.equals(id.getComponentType())
                || URoComponentType.LED.equals(id.getComponentType())
                || URoComponentType.SPEAKER.equals(id.getComponentType())) {
            return false;
        }
        if(id.getId() < 1 || id.getId() > 8) {
            return false;
        }
        if(URoSubscribeSensorMode.THRESHOLD.equals(mode) || URoSubscribeSensorMode.OFFSET.equals(mode)) {
            if(URoComponentType.ENVIRONMENTSENSOR.equals(id.getComponentType())) {
                return values.size() == 2;
            } else {
                return values.size() == 1;
            }
        } else if(URoSubscribeSensorMode.TIME.equals(mode)) {
            return values.size() == 1;
        }
        return true;
    }

    public URoComponentID getId() {
        return id;
    }

    public URoSubscribeSensorMode getMode() {
        return mode;
    }

    public int[] getValues() {
        int[] result = new int[values.size()];
        for(int i = 0; i < values.size(); i++) {
            result[i] = values.get(i);
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof URoSubscribeSensorInfo)) return false;

        URoSubscribeSensorInfo that = (URoSubscribeSensorInfo) o;

        return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    public enum URoSubscribeSensorMode {
        NONE(0),
        TIME(1),
        THRESHOLD(2),
        OFFSET(3);

        private int mode;

        public int getMode() {
            return mode;
        }

        URoSubscribeSensorMode(int mode) {
            this.mode = mode;
        }
    }

}
