/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.blockly;

import com.ubtedu.ukit.common.files.FileHelper;

/**
 * @Author qinicy
 * @Date 2018/12/19
 **/
public class BlocklySound {
    /**
     * type(animal,machine,recording,animal和machine为系统音效,recording为自定义音效)
     */
    public static final String TYPE_ANIMAL = "animal";
    public static final String TYPE_MACHINE = "machine";
    public static final String TYPE_RECORDING = "recording";
    /**
     *  官方音效路径
     */
    public static final String SAMPLE_SOUND_PATH = FileHelper.join(
            FileHelper.getPublicBlocklyPath(),
            "resource",
            "images",
            "popup",
            "soundEffects",
            "source"
    );


    public String key;
    public String icon;
    public String type;        // 类型，分系统和自定义，系统的又分为
    public String duration;
    public String description;
    public String path;
    public boolean isDelay = false;    // 是否延时唤醒Blockly
    public String branchId;    // 分支ID

    public BlocklySound() {

    }

    public BlocklySound(String type, String key, String description, String icon) {
        this.type = type;
        this.key = key;
        this.icon = icon;
        this.description = description;

    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BlocklySound)) {
            return false;
        }
        BlocklySound sound = (BlocklySound) obj;
        if (!this.type.equals(sound.type)) {
            return false;
        }
        if (!this.key.equals(sound.key)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
