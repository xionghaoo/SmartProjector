package com.ubtedu.deviceconnect.libs.ukit.smart.model;

/**
 * @Author naOKi
 * @Date 2019/12/18
 **/
public class URoAudioPlayResult {
    private URoAudioPlayState state;
    private int sessionId = 0;

    public URoAudioPlayResult(URoAudioPlayState state) {
        this.state = state;
        this.sessionId = sessionId;
    }

    public URoAudioPlayState getState() {
        return state;
    }

    public void setState(URoAudioPlayState state) {
        this.state = state;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "URoAudioPlayResult{" +
                "sessionId='" + String.format("%08x", sessionId) + "'" +
                ", state='" + state.getDescription() + "[" + state.getCode() + "]'" +
                '}';
    }
}
