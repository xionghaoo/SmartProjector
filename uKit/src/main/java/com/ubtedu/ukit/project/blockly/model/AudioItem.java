package com.ubtedu.ukit.project.blockly.model;

/**
 * @Author naOKi
 * @Date 2019/12/17
 **/
public abstract class AudioItem implements Comparable<AudioItem> {

    public abstract String getFilePath();
    public abstract long getCreateTime();
    public abstract long getDuration();
    public abstract String getName();
    public abstract boolean setName(String name);
    public abstract String getId();

    @Override
    public int compareTo(AudioItem o) {
        return Long.compare(o.getCreateTime(), this.getCreateTime());
    }

}
