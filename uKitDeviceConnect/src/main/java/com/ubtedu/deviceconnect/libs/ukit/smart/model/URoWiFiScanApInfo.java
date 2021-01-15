package com.ubtedu.deviceconnect.libs.ukit.smart.model;

import androidx.annotation.NonNull;

/**
 * @Author naOKi
 * @Date 2019/10/22
 **/
public class URoWiFiScanApInfo implements Comparable<URoWiFiScanApInfo> {

    private final String ssid;
    private final int rssi;
    private final URoWiFiAuthMode authMode;

    public URoWiFiScanApInfo(@NonNull String ssid, int rssi, @NonNull URoWiFiAuthMode authMode) {
        this.ssid = ssid;
        this.rssi = rssi;
        this.authMode = authMode;
    }

    public String getSsid() {
        return ssid;
    }

    public int getRssi() {
        return rssi;
    }

    public URoWiFiAuthMode getAuthMode() {
        return authMode;
    }

    @Override
    public String toString() {
        return "URoWiFiScanApInfo{" +
                "ssid='" + ssid + '\'' +
                ", rssi=" + rssi +
                ", authMode=" + authMode +
                '}';
    }

    @Override
    public int compareTo(URoWiFiScanApInfo uRoWiFiScanApInfo) {
        return Integer.compare(getRssi(), uRoWiFiScanApInfo.getRssi());
    }

}
