/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.blockly;


import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.ukit.project.blockly.audio.AudioRecorder;
import com.ubtedu.ukit.project.blockly.model.AudioItem;
import com.ubtedu.ukit.project.blockly.model.LocalAudioItem;

/**
 * @Author naOKi
 * @Date 2019/12/17
 **/
public class LocalAudioRecordPresenter extends AudioRecordPresenter {

    public LocalAudioRecordPresenter(AudioRecordUI ui) {
        super(ui);
    }

    @Override
    public AudioItem newAudioImpl() {
        return new LocalAudioItem(new BlocklyAudio());
    }

    @Override
    public boolean isRecordingImpl() {
        return AudioRecorder.getInstance().isRecording();
    }

    @Override
    public boolean startRecordAudioImpl() {
        AudioRecorder.getInstance().setOnRecordCallback(mRecordCallback);
        return AudioRecorder.getInstance().startRecord(getAudio().getFilePath());
    }

    @Override
    public void stopRecordAudioImpl() {
        if (AudioRecorder.getInstance().isRecording()) {
            AudioRecorder.getInstance().stopRecord();
        }
    }

    @Override
    public void cancelImpl() {
        AudioRecorder.getInstance().setOnRecordCallback(null);
        if (AudioRecorder.getInstance().isRecording()) {
            AudioRecorder.getInstance().stopRecord();
        }
        notifyRecordFinish(false, null, null);
    }

    @Override
    protected void initImpl() {

    }

    @Override
    public void releaseImpl() {
        AudioRecorder.getInstance().setOnRecordCallback(null);
    }

    private AudioRecorder.OnRecordCallback mRecordCallback = new AudioRecorder.OnRecordCallback() {
        @Override
        public void onRecordCompleted(boolean isSuccess) {
            if (getAudio() != null && getAudio() instanceof LocalAudioItem) {
                ((LocalAudioItem)getAudio()).getBlocklyAudio().duration = getRecordProgressTime();
            }
            notifyRecordFinish(isSuccess, null, getAudio());
            LogUtil.d("onRecordCompleted success:" + isSuccess);
        }
    };

}
