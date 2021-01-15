/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.bridge.arguments;

import com.google.gson.annotations.SerializedName;

/**
 * @Author qinicy
 * @Date 2018/12/21
 **/
public class ScreenBorder {
    public ScreenBorder(float left, float right, float top, float bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    @SerializedName("left")
    public float left;
    @SerializedName("right")
    public float right;
    @SerializedName("top")
    public float top;
    @SerializedName("bottom")
    public float bottom;
}
