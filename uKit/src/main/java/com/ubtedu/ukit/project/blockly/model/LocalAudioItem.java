package com.ubtedu.ukit.project.blockly.model;

import androidx.annotation.NonNull;

import com.ubtedu.ukit.project.blockly.BlocklyAudio;

/**
 * @Author naOKi
 * @Date 2019/12/17
 **/
public class LocalAudioItem extends AudioItem {

    private BlocklyAudio blocklyAudio;

    public LocalAudioItem(@NonNull BlocklyAudio blocklyAudio) {
        this.blocklyAudio = blocklyAudio;
    }

    public BlocklyAudio getBlocklyAudio() {
        return blocklyAudio;
    }

    @Override
    public String getFilePath() {
        return blocklyAudio.getFilePathInWorkspace();
    }

    @Override
    public long getCreateTime() {
        return blocklyAudio.createTime;
    }

    @Override
    public long getDuration() {
        return blocklyAudio.duration;
    }

    @Override
    public String getName() {
        return blocklyAudio.name;
    }

    @Override
    public boolean setName(String name) {
        blocklyAudio.name = name;
        return true;
    }

    @Override
    public String getId() {
        return blocklyAudio.id;
    }

}
