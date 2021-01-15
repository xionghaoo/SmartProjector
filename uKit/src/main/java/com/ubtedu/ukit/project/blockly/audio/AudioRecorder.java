/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.blockly.audio;

import android.media.MediaRecorder;
import android.util.Log;

import com.ubtedu.ukit.common.files.FileHelper;

import java.io.File;
import java.io.IOException;

/**
 * Copy from UBETECH EDU
 *
 * @Author qinicy
 * @Date 2018/12/19
 **/
public class AudioRecorder {

    private final String TAG = this.getClass().getSimpleName();
    private static AudioRecorder sAudioRecorder;
    private MediaRecorder mRecorder;
    private String mFilePath;
    private AAC mAAC;
    private long mLastRecordTime = 0;
    private boolean isRecording;
    private OnRecordCallback mOnRecordCallback;
    private static class SingletonHolder {
        private final static AudioRecorder instance = new AudioRecorder();
    }

    public static AudioRecorder getInstance() {
        return AudioRecorder.SingletonHolder.instance;
    }


    private AudioRecorder() {
        //
    }

    public void setOnRecordCallback(OnRecordCallback onRecordCallback) {
        mOnRecordCallback = onRecordCallback;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public boolean startRecord(final String filePath) {
        Log.i(TAG, "startRecord：" + filePath);
        mFilePath = filePath;
        boolean success = startRecord();
        Log.i(TAG, "startRecord：" + success);
        isRecording = success;
        return success;
    }

    private boolean startRecord() {
        if (isRecording) {
            stopRecord();
        }
        long current = System.currentTimeMillis();
        if (current - mLastRecordTime < 500) {
            return false;
        }
        mLastRecordTime = current;
        return aacRecord();
    }

    public void stopRecord() {
        release();
        if (isRecording) {
            onRecordSuccess();
        }
    }

    public void release() {
        if (mRecorder != null) {
            try {
                mRecorder.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } finally {
                mRecorder = null;
            }
        }
    }

    public String getFilePath() {
        return mFilePath;
    }

    private class RecorderOnErrorListener implements MediaRecorder.OnErrorListener {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Log.e(TAG, "录音失败：what=" + what + " extra=" + extra);
            onRecordFailed();
        }
    }

    private class RecorderInfoListener implements MediaRecorder.OnInfoListener {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            switch (what) {
                case MediaRecorder.MEDIA_RECORDER_INFO_UNKNOWN:
                    Log.e(TAG, "OnInfo: MEDIA_RECORDER_INFO_UNKNOWN");
                    break;
                case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
                    Log.e(TAG, "OnInfo: MEDIA_RECORDER_INFO_MAX_DURATION_REACHED");
                    break;
                case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:
                    Log.e(TAG, "OnInfo: MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED");
                    break;
            }
        }
    }


    private boolean aacRecord() {
        boolean result = false;
        try {
            release();
            checkFilePath();
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mRecorder.setAudioSamplingRate(8000);
            mRecorder.setAudioChannels(1);
            mRecorder.setOutputFile(mFilePath);
            mRecorder.setOnErrorListener(new RecorderOnErrorListener());
            mRecorder.setOnInfoListener(new RecorderInfoListener());
            mRecorder.prepare();
            mRecorder.start();
            result = true;
        } catch (IOException e) {
            onRecordFailed();
            e.printStackTrace();
            release();
            Log.e("Audio", "prepare() failed");
        } catch (IllegalStateException e) {
            onRecordFailed();
            e.printStackTrace();
            release();
            Log.e("Audio", "prepare() failed");
        }
        return result;
    }

    private void checkFilePath() {
        File audio = new File(mFilePath);
        File path = audio.getParentFile();
        if (!path.exists()) {
            path.mkdirs();
        }
    }

    private void onRecordSuccess() {
        isRecording = false;
        if (mOnRecordCallback != null) {
            mOnRecordCallback.onRecordCompleted(true);
        }
    }

    private void onRecordFailed() {
        isRecording = false;
        File audioFile = new File(mFilePath);
        File audioFolder = audioFile.getParentFile();
        if (audioFolder.exists()) {
            try {
                FileHelper.removeDir(audioFolder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mOnRecordCallback != null) {
            mOnRecordCallback.onRecordCompleted(false);
        }
    }

    public interface OnRecordCallback {
        void onRecordCompleted(boolean isSuccess);
    }
}
