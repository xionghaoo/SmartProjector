/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.bridge.arguments;

import com.google.gson.annotations.SerializedName;
import com.ubtedu.bridge.BridgeBoolean;
import com.ubtedu.ukit.BuildConfig;
import com.ubtedu.ukit.application.ServerConfig;

import java.io.Serializable;

/**
 * @Author qinicy
 * @Date 2018/12/5
 **/
public class AppInfoArguments implements Serializable {
    @SerializedName("appKey")
    public String appKey = "";
    @SerializedName("appId")
    public String appId = "";
    @SerializedName("deviceId")
    public String deviceId = "";
    @SerializedName("authorization")
    public String authorization = "x";
    @SerializedName("logEnabled")
    public int logEnabled = BridgeBoolean.wrap(BuildConfig.DEBUG);

    @SerializedName("tutorialServer")
    public String tutorialServer = ServerConfig.getTutorialServer();

}
