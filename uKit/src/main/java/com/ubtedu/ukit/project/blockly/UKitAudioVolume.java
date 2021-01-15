package com.ubtedu.ukit.project.blockly;

/**
 * @Author naOKi
 * @Date 2020/03/16
 **/
public enum UKitAudioVolume {
    VERY_LOW(42),
    LOW(56),
    MEDIUM(70),
    HIGH(84),
    VERY_HIGH(98);
    int volume;

    public int getVolume() {
        return volume;
    }

    UKitAudioVolume(int volume) {
        this.volume = volume;
    }
}
