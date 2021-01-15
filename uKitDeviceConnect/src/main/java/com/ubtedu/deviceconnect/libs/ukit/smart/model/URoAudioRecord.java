package com.ubtedu.deviceconnect.libs.ukit.smart.model;

/**
 * @Author naOKi
 * @Date 2019/10/22
 **/
public class URoAudioRecord {

    private String name;
    private long duration;
    private long date;
    private int sessionId = 0;
    private URoAudioRecordState state;

    public URoAudioRecord(String name, long duration, long date, URoAudioRecordState state) {
        this.name = name;
        this.duration = duration;
        this.date = date;
        this.state = state;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public URoAudioRecordState getState() {
        return state;
    }

    public void setState(URoAudioRecordState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "URoAudioRecord{" +
                "sessionId='" + String.format("%08x", sessionId) + "'"+
                ", name='" + name + "'" +
                ", duration=" + duration +
                ", date=" + date +
                ", state=" + state +
                '}';
    }

}
