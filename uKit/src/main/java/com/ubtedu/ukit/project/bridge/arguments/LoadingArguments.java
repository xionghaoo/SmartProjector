/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.bridge.arguments;

import com.google.gson.annotations.SerializedName;
import com.ubtedu.bridge.BridgeBoolean;

import java.io.Serializable;

/**
 * @Author qinicy
 * @Date 2018/12/4
 **/
public class LoadingArguments implements Serializable {
    public final static String ANIM_REFRESH = "refresh";
    @SerializedName("message")
    public String message;
    @SerializedName("anim")
    public String anim;
    @SerializedName("isCancelable")
    public Number isCancelable = BridgeBoolean.TRUE();
}
