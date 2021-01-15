/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.blockly;

import androidx.annotation.NonNull;

/**
 * @Author naOKi
 * @Date 2019/12/17
 **/
public class LocalAudioRecordDialogFragment extends AudioRecordDialogFragment {

    @NonNull
    @Override
    protected AudioRecordPresenter createPresenter(AudioRecordUI ui) {
        return new LocalAudioRecordPresenter(ui);
    }

}
