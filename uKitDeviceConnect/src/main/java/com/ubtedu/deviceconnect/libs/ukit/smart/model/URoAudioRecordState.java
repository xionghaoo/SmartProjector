package com.ubtedu.deviceconnect.libs.ukit.smart.model;

/**
 * @Author naOKi
 * @Date 2019/12/18
 **/
public enum URoAudioRecordState {
    COMPLETE(0, "自动结束录音时长"),
    FAIL(1, "异常"),
    START_SUCCESS(2, "启动录音"),
    START_FAILURE(3, "启动录音失败"),
    STOP_SUCCESS(4, "停止录音"),
    STOP_FAILURE(5, "停止录音失败");

    private String description;
    private int code;

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }

    URoAudioRecordState(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static URoAudioRecordState findByCode(int code) {
        for (URoAudioRecordState state : URoAudioRecordState.values()) {
            if (state.getCode() == code) {
                return state;
            }
        }
        return URoAudioRecordState.FAIL;
    }
}
