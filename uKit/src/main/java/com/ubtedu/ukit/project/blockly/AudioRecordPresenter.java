/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.blockly;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.alpha1x.utils.TimeUtil;
import com.ubtedu.ukit.project.blockly.model.AudioItem;

/**
 * @Author qinicy
 * @Date 2019/4/17
 **/
public abstract class AudioRecordPresenter {

    protected final static int RECORD_TIME_MAX = 15000;
    protected final static int RECORD_TIMEOUT_MAX = 20000;
    private final static int RECORD_TIME_UNIT = 1000;
    private long mRecordStartTime;

    private Handler mHandler;
    private AudioRecordUI mUI;
    private AudioItem mAudio;
    protected boolean cancelFlag = false;
    private boolean released = false;

    public AudioRecordPresenter(AudioRecordUI ui) {
        mUI = ui;
    }

    public AudioRecordUI getUI() {
        return mUI;
    }

    private void initTimeHandler() {
        if(mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    long recordTime = getRecordProgressTime();
                    if (recordTime < RECORD_TIME_MAX) {
                        mHandler.sendEmptyMessageDelayed(0, RECORD_TIME_UNIT);
                    } else {
                        stopRecordAudio();
                    }
                    postUpdateRecordProgress();
                }
            };
        }
    }

    protected long getRecordProgressTime() {
        return Math.min(System.currentTimeMillis() - mRecordStartTime, RECORD_TIME_MAX);
    }

    private void postUpdateRecordProgress() {
        String time = TimeUtil.milliseconds2String(getRecordProgressTime());
        if(mUI != null) {
            mUI.updateRecordProgress(time);
        }
    }

    private void startRecordTimer() {
        if (mHandler == null) {
            initTimeHandler();
        }
        mRecordStartTime = System.currentTimeMillis();
        mHandler.sendEmptyMessageDelayed(0, RECORD_TIME_UNIT);
    }

    public AudioItem getAudio() {
        return mAudio;
    }

    public boolean isRecording() {
        return isRecordingImpl();
    }

    public void startRecordAudio() {
        stopRecordAudioImpl();
        mAudio = newAudioImpl();
        boolean isStarted = startRecordAudioImpl();
        if (isStarted) {
            startRecordTimer();
            if(mUI != null) {
                mUI.onRecordStarted();
            }
        }
        LogUtil.d("startRecord:" + mAudio.getFilePath() + "  isStarted:" + isStarted);
    }

    public void stopRecordAudio() {
        if(mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        stopRecordAudioImpl();
    }

    public void cancel() {
        if (cancelFlag){
            return;
        }
        cancelFlag = true;
        mAudio = null;
        if(mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        cancelImpl();
    }

    public void init() {
        initImpl();
    }

    public void release() {
        if(mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        releaseImpl();
        released = true;
    }

    protected void notifyRecordFinish(boolean isSuccess, String errorMsg, AudioItem audioItem) {
        if(mUI != null && !released) {
            if (cancelFlag) {
                mUI.onRecordFinish(false, null, null);
            } else {
                mUI.onRecordFinish(isSuccess, errorMsg, audioItem);
            }
        }
    }

    protected abstract AudioItem newAudioImpl();
    protected abstract boolean isRecordingImpl();
    protected abstract boolean startRecordAudioImpl();
    protected abstract void stopRecordAudioImpl();
    protected abstract void cancelImpl();
    protected abstract void initImpl();
    protected abstract void releaseImpl();

}
