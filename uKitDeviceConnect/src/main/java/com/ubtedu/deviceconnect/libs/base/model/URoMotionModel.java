package com.ubtedu.deviceconnect.libs.base.model;

import java.util.ArrayList;

public class URoMotionModel {
    private String name;
    private ArrayList<URoFrameModel> frames;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<URoFrameModel> getFrames() {
        return frames;
    }

    public void setFrames(ArrayList<URoFrameModel> frames) {
        this.frames = frames;
    }
}
