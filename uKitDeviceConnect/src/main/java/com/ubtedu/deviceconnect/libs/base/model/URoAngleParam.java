package com.ubtedu.deviceconnect.libs.base.model;

import com.google.gson.annotations.SerializedName;

/**
 * @Author naOKi
 * @Date 2018/11/12
 **/
public class URoAngleParam implements Comparable<URoAngleParam> {
    @SerializedName("servo")
    public int id;
    @SerializedName("degree")
    public int angle;
    public URoAngleParam(int id, int angle) {
        this.id = id;
        this.angle = angle;
    }
    @Override
    public int compareTo(URoAngleParam o) {
        return Integer.compare(id, o.id);
    }
    @Override
    public String toString() {
        return "URoAngleParam{" +
                "id=" + id +
                ", angle=" + angle +
                '}';
    }
}
