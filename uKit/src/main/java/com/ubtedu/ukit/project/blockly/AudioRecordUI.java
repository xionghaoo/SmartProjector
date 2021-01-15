/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.blockly;

import com.ubtedu.ukit.project.blockly.model.AudioItem;

/**
 * @Author qinicy
 * @Date 2019/4/17
 **/
public interface AudioRecordUI {
    void updateRecordProgress(String second);

    void onRecordStarted();

    void onRecordFinish(boolean success, String msg, AudioItem audioItem);

    void onRecordProcessing();

}
