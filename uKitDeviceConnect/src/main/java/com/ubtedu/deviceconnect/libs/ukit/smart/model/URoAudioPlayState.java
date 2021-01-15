package com.ubtedu.deviceconnect.libs.ukit.smart.model;

/**
 * @Author naOKi
 * @Date 2019/12/18
 **/
public enum URoAudioPlayState {
    COMPLETE(0, "自动播放完成"),
    FAIL(1, "异常"),
    START_SUCCESS(2, "启动播放"),
    START_FAILURE(3, "启动播放失败"),
    STOP_SUCCESS(4, "停止播放"),
    STOP_FAILURE(5, "停止播放失败");

    private String description;
    private int code;

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }

    URoAudioPlayState(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static URoAudioPlayState findByCode(int code) {
        for (URoAudioPlayState state : URoAudioPlayState.values()) {
            if (state.getCode() == code) {
                return state;
            }
        }
        return URoAudioPlayState.FAIL;
    }
}
