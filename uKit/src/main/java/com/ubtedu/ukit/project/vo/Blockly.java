/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.vo;

import com.google.gson.annotations.SerializedName;

/**
 * @Author qinicy
 * @Date 2018/11/5
 **/
public class Blockly extends ProjectFile {
    public final static String WORKSPACE_FILE_NAME = "workspace.xml";
    public final static String TYPE_NORMAL = "normal";
    public final static String TYPE_CONTROLLER_BUTTON = "controller-button";
    public final static String TYPE_CONTROLLER_SLIDER = "controller-slider";
    public final static String TYPE_CONTROLLER_SWITCHTOGGLE = "controller-switchToggle";
    public final static String TYPE_CONTROLLER_SWITCHTOUCH = "controller-switchTouch";
    @SerializedName("workspace")
    public String workspace;
}
