package com.ubtedu.ukit.project.blockly.model;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoAudioRecord;

/**
 * @Author naOKi
 * @Date 2019/12/17
 **/
public class UKitAudioItem extends AudioItem {

    private URoAudioRecord audioRecord;

    public UKitAudioItem(@NonNull URoAudioRecord audioRecord) {
        this.audioRecord = audioRecord;
    }

    public URoAudioRecord getAudioRecord() {
        return audioRecord;
    }

    @Override
    public String getFilePath() {
        return "file://spiffs/record/" + audioRecord.getName() + ".amr";
    }

    @Override
    public long getCreateTime() {
        return audioRecord.getDate();
    }

    @Override
    public long getDuration() {
        return audioRecord.getDuration();
    }

    public void setDuration(long duration) {
        audioRecord.setDuration(duration);
    }

    @Override
    public String getName() {
        return audioRecord.getName();
    }

    @Override
    public boolean setName(String name) {
        audioRecord.setName(name);
        return true;
    }

    @Override
    public String getId() {
        return audioRecord.getName();
    }

}
