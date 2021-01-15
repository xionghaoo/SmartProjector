package com.ubtedu.deviceconnect.libs.base.model;

import java.util.HashMap;

public class URoFrameModel {
    private HashMap<Integer, Integer> idAngleDictionary;
    private int time;
    private int waitTime;

    public HashMap<Integer, Integer> getIdAngleDictionary() {
        return idAngleDictionary;
    }

    public void setIdAngleDictionary(HashMap<Integer, Integer> idAngleDictionary) {
        this.idAngleDictionary = idAngleDictionary;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }
}
