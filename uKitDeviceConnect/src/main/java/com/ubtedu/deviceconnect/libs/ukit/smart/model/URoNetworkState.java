package com.ubtedu.deviceconnect.libs.ukit.smart.model;

/**
 * @Author naOKi
 * @Date 2019/10/22
 **/
public enum URoNetworkState {

    IDLE(0, "空闲"),
    CONNECTED(1, "已连接"),
    DISCONNECTED(2, "断开连接");

    private String description;
    private int code;

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }

    URoNetworkState(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static URoNetworkState findByCode(int code) {
        for(URoNetworkState state : URoNetworkState.values()) {
            if(state.getCode() == code) {
                return state;
            }
        }
        return URoNetworkState.IDLE;
    }

}
