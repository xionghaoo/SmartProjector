/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.blockly.audio;

import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * @Author qinicy
 * @Date 2018/12/19
 **/
public class AudioPlayer {
    private final String TAG = this.getClass().getSimpleName();
    private MediaPlayer mPlayer = null;
    private boolean isPausing = false;
    private int progress = -1;
    private String source;

    private static AudioPlayer player;

    private IProgressListener listener;

    private static class SingletonHolder {
        private final static AudioPlayer instance = new AudioPlayer();
    }

    public static AudioPlayer getInstance() {
        return AudioPlayer.SingletonHolder.instance;
    }


    private AudioPlayer() {
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i(TAG, "onCompletion");
                //播放结束，回调结果给开启播放者
                if (listener != null) {
                    IProgressListener cb = listener;
                    listener = null;
                    cb.onFinished();
                }
            }
        });
        mPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                Log.i(TAG, "onSeekComplete");
            }
        });
        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e(TAG, "播放onError:" + "what=" + what + " extra=" + extra);
                //产生错误，回调错误结果给开启播放者
                if (listener != null) {
                    IProgressListener cb = listener;
                    listener = null;
                    cb.onError();
                }
                return false;
            }
        });
        mPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                Log.e(TAG, "onInfo:" + "what=" + what + " extra=" + extra);
                return false;
            }
        });
    }

    private boolean play(String source) {
        File file = new File(source);
        if (!file.exists()) {
            return false;
        }
        try {
            mPlayer.reset();
            mPlayer.setDataSource(source);
            mPlayer.prepare();
            if (!TextUtils.isEmpty(this.source)) {
                if (this.source.equals(source)) {
                    if (isPausing) {
                        if (progress > -1) {
                            mPlayer.seekTo(progress);
                            progress = -1;
                            isPausing = false;
                        }
                    } else {
                        this.source = source;
                        progress = -1;
                        isPausing = false;
                    }
                }
            } else {
                this.source = source;
                progress = -1;
                isPausing = false;
            }
            mPlayer.start();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean play(String source, IProgressListener listener) {
        synchronized (AudioPlayer.class) {
            //如果正在播放，而且设置了回调，则需要把结果回调给上一个开启播放者，然后再开始播放，否则上一个开启播放者就接收不到播放结束事件，也不知道被中断了
            if (this.listener != null && mPlayer.isPlaying()) {
                this.listener.onError();
            }
            this.listener = listener;
            boolean result = play(source);
            if (!result) {
                //开启播放失败，回调失败
                this.listener.onError();
            }
            return result;
        }
    }

    public void pause() {
        synchronized (AudioPlayer.class) {
            try {
                progress = mPlayer.getCurrentPosition();
                isPausing = true;
                mPlayer.pause();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        synchronized (AudioPlayer.class) {
            if (listener != null && mPlayer != null && mPlayer.isPlaying()) {
                //停止播放，如果正在播放，而且设置了回调，则回调结果给开启播放者
                IProgressListener cb = listener;
                listener = null;
                cb.onFinished();
            }
            try {
                source = null;
                if (mPlayer != null) {
                    mPlayer.stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void release() {
        synchronized (AudioPlayer.class) {
            if (listener != null && mPlayer != null && mPlayer.isPlaying()) {
                //释放播放器实例，如果正在播放，而且设置了回调，则回调结果给开启播放者
                IProgressListener cb = listener;
                listener = null;
                cb.onFinished();
            }
            if (mPlayer != null) {
                mPlayer.release();
                mPlayer = null;
            }
        }
    }

    public interface IProgressListener {
        public void onFinished();

        public void onError();
    }
}
