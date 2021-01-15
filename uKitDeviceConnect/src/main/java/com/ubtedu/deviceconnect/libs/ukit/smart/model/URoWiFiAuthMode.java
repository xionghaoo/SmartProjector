package com.ubtedu.deviceconnect.libs.ukit.smart.model;

/**
 * @Author naOKi
 * @Date 2019/10/22
 **/
public enum URoWiFiAuthMode {
    NONE(0),
    WEP(1),
    WPA_PSK(2),
    WPA2_PSK(3),
    WPA_WPA2_PSK(4),
    WPA2_ENTERPRISE(5),
    WIFI_AUTH_MAX(6);

    private int code;

    public int getCode() {
        return code;
    }

    URoWiFiAuthMode(int code) {
        this.code = code;
    }

    public static URoWiFiAuthMode findByCode(int code) {
        for(URoWiFiAuthMode authMode : URoWiFiAuthMode.values()) {
            if(authMode.getCode() == code) {
                return authMode;
            }
        }
        return URoWiFiAuthMode.NONE;
    }
}
