/**
 * ©2018, UBTECH Robotics, Inc. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.vo;

import com.google.gson.annotations.SerializedName;
import com.ubtedu.ukit.common.vo.SerializablePOJO;

/**
 * @Author naOKi
 * @Date 2018/12/07
 **/
public class BlocklyFile extends SerializablePOJO {

    public final static String LUA_SUFFIX = ".lua";
    public final static String PYTHON_SUFFIX = ".py";
    public static final String NAME_ON = "on" + LUA_SUFFIX;
    public static final String NAME_OFF = "off" + LUA_SUFFIX;
    public static final String NAME_DOWN = "down" + LUA_SUFFIX;
    public static final String NAME_UP = "up" + LUA_SUFFIX;
    public static final String NAME_CHANGE = "change" + LUA_SUFFIX;
    public static final String NAME_MICRO_PYTHON = "micropython" + PYTHON_SUFFIX;

    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("content")
    public String content;

    public String getFileName(){
        return name;
    }

}
