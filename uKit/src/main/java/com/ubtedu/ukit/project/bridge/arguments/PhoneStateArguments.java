/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.bridge.arguments;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @Author qinicy
 * @Date 2018/12/24
 **/
public class PhoneStateArguments {
    @SerializedName("state")
    public String state;
    @SerializedName("values")
    public List<float[]> values;
}
