/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.bridge;

/**
 *  * 手机方向
 *  * 横屏情况下(手机方向为上方)：
 *  * 左   右(绕Y轴旋转)  前    后(绕X轴)
 *  * + -           +  -
 * @Author qinicy
 * @Date 2018/12/24
 **/
public enum DeviceDirection {

    NONE("none", 0),
    LEFT("left", 1),
    RIGHT("right", 2),
    UP("up", 3),
    DOWN("down", 4),
    SWING("swing", 5);


    DeviceDirection(String value, int type){
        this.value = value;
        this.type = type;
    }

    private String value;
    private int type;

    public String getValue(){
        return this.value;
    }
    public int getType(){
        return type;
    }

}