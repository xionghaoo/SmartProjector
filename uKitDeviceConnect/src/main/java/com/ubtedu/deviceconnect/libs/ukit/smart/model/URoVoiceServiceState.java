package com.ubtedu.deviceconnect.libs.ukit.smart.model;

/**
 * @Author naOKi
 * @Date 2019/12/18
 **/
public enum URoVoiceServiceState {
    COMPLETE(0, "完成"),
    BAD_REQUEST(400, "请求格式不正确"),
    FORBIDDEN(403, "认证失败"),
    UNSUPPORTED_MEDIATYPE(415, "服务器不支持多媒体类型"),
    TOO_MANY_REQUESTS(429, "请求太频繁"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用");

    private String description;
    private int code;

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }

    URoVoiceServiceState(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static URoVoiceServiceState findByCode(int code) {
        for (URoVoiceServiceState state : URoVoiceServiceState.values()) {
            if (state.getCode() == code) {
                return state;
            }
        }
        return URoVoiceServiceState.INTERNAL_SERVER_ERROR;
    }
}
