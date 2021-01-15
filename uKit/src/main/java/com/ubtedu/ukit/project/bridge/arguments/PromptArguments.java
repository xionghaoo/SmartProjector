/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.bridge.arguments;

import com.google.gson.annotations.SerializedName;
import com.ubtedu.bridge.BridgeBoolean;

import java.io.Serializable;

/**
 * @Author qinicy
 * @Date 2018/11/12
 **/
public class PromptArguments implements Serializable {
    @SerializedName("type")
    public Number type;
    @SerializedName("title")
    public String title;
    @SerializedName("message")
    public String message;
    @SerializedName("negativeText")
    public String negativeText;
    @SerializedName("positiveText")
    public String positiveText;
    @SerializedName("isShowPositiveButton")
    public Number isShowPositiveButton = BridgeBoolean.TRUE();
    @SerializedName("isShowNegativeButton")
    public Number isShowNegativeButton = BridgeBoolean.TRUE();
    @SerializedName("isCancelable")
    public Number isCancelable = BridgeBoolean.TRUE();
}
