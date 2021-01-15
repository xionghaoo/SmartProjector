package com.ubtedu.deviceconnect.libs.base.model;

public class URoSpeakerInfo {
    private String name;
    private String mac;

    public URoSpeakerInfo(String name, String mac) {
        this.name = name;
        this.mac = mac;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    protected void setMac(String mac) {
        this.mac = mac;
    }

    @Override
    public String toString() {
        return "URoSpeakerInfo{" +
                "name='" + name + '\'' +
                ", mac='" + mac + '\'' +
                '}';
    }

}
