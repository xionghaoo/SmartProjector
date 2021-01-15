/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.blockly;


import com.ubtedu.ukit.project.blockly.model.AudioItem;

import java.util.ArrayList;

/**
 * @Author naOKi
 * @Date 2019/12/17
 **/
public interface AudioListUI {
    void updateAudioList(ArrayList<AudioItem> list);
    void showAudioRecordUI();
    void showAudioRecordFullUI();
    void changeUIStatus(UIStatus status);
    void dismissUI();
    
    enum UIStatus {
        LOADING, FAILURE, SUCCESS
    }
}
