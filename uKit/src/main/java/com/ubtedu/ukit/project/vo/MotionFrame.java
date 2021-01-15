/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.vo;

import com.google.gson.annotations.SerializedName;
import com.ubtedu.deviceconnect.libs.base.model.URoAngleParam;

import java.io.Serializable;
import java.util.List;

/**
 * @Author qinicy
 * @Date 2018/11/5
 **/
public class MotionFrame implements Serializable {
    @SerializedName("name")
    public String name;
    @SerializedName("time")
    public int time;
    @SerializedName("waittime")
    public int waittime;
    @SerializedName("duration")
    public long duration;
    @SerializedName("angles")
    public List<Integer> angles;
    @SerializedName("actived")
    public List<Integer> actived;

    @SerializedName("angleParam")
    List<URoAngleParam> angleParam;


}
