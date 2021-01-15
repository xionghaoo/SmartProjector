/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.blockly;

import androidx.annotation.NonNull;

/**
 * @Author naOKi
 * @Date 2019/12/17
 **/
public class UKitAudioRecordDialogFragment extends AudioRecordDialogFragment {

    @NonNull
    @Override
    protected AudioRecordPresenter createPresenter(AudioRecordUI ui) {
        return new UKitAudioRecordPresenter(ui);
    }

}
