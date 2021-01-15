package com.ubtedu.deviceconnect.libs.ukit.smart.model;

/**
 * @Author naOKi
 * @Date 2019/12/18
 **/
public class URoAsrResult {

    private final URoVoiceServiceState serviceState;
    private final boolean isMatch;
    private final String keyword;

    public URoAsrResult(URoVoiceServiceState serviceState, boolean isMatch, String keyword) {
        this.serviceState = serviceState;
        this.isMatch = isMatch;
        this.keyword = keyword;
    }

    public URoVoiceServiceState getServiceState() {
        return serviceState;
    }

    public boolean isMatch() {
        return isMatch;
    }

    public String getKeyword() {
        return keyword;
    }

}
