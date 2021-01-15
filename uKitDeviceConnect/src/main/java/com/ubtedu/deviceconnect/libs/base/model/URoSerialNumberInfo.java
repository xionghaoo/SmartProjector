package com.ubtedu.deviceconnect.libs.base.model;

/**
 * @Author naOKi
 * @Date 2019/02/13
 **/
public abstract class URoSerialNumberInfo {

    public static final URoSerialNumberInfo INVALID = new URoSerialNumberInfo(){};

    public boolean valid = false;
    public String serialNumber = "";

    public boolean isValid() {
        return valid;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    @Override
    public String toString() {
        return "URoSerialNumberInfo{" +
                "valid=" + valid +
                ", serialNumber='" + serialNumber + '\'' +
                '}';
    }

    public static final URoSerialNumberInfo EMPTY_DATA = new URoSerialNumberInfo(){};

}
