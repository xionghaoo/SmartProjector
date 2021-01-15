/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.controller.model.config;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * @Author qinicy
 * @Date 2019/1/25
 **/
public class WidgetConfigWrapper {
    public WidgetConfigWrapper(ArrayList<WidgetConfig> configs) {
        this.configs = configs;
    }

    @SerializedName("WidgetConfig")
    public ArrayList<WidgetConfig> configs = new ArrayList<>();
}
