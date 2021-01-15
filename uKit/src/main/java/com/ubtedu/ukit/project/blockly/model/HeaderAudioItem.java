package com.ubtedu.ukit.project.blockly.model;

/**
 * @Author naOKi
 * @Date 2019/12/17
 **/
public class HeaderAudioItem extends AudioItem {

    @Override
    public String getFilePath() {
        return null;
    }

    @Override
    public long getCreateTime() {
        return Long.MAX_VALUE;
    }

    @Override
    public long getDuration() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean setName(String name) {
        return false;
    }

    @Override
    public String getId() {
        return null;
    }
}
