/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.vo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @Author qinicy
 * @Date 2018/11/5
 **/
public class Motion extends ProjectFile {
    public final static String MOTION_FILE_SUFFIX = ".json";
    @SerializedName("totaltime")
    public long totaltime;
    @SerializedName("frames")
    public List<MotionFrame> frames;
    @SerializedName("servos")
    public List<Integer> servos;

    public String getMotionFileName() {
        return id + MOTION_FILE_SUFFIX;
    }
}
