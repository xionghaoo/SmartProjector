/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.bridge.arguments;

import com.google.gson.annotations.SerializedName;
import com.ubtedu.bridge.BridgeBoolean;
import com.ubtedu.ukit.bluetooth.UkitDeviceCompact;
import com.ubtedu.ukit.application.ServerConfig;
import com.ubtedu.ukit.common.utils.PlatformUtil;

import java.io.Serializable;

/**
 * @Author qinicy
 * @Date 2018/12/5
 **/
public class ConfigArguments implements Serializable {
    @SerializedName("language")
    public String language;
    @SerializedName("tagName")
    public String tagName;
    @SerializedName("devicePlatform")
    public String devicePlatform = PlatformUtil.PLATFORM_ANDROID;
    @SerializedName("hasAccelerometerSensor")
    public int hasAccelerometerSensor = BridgeBoolean.wrap(true);
    @SerializedName("deviceVersion")
    public int deviceVersion = UkitDeviceCompact.UKIT_LEGACY_DEVICE;
    @SerializedName("tutorialServer")
    public String tutorialServer = ServerConfig.getTutorialServer();
}
