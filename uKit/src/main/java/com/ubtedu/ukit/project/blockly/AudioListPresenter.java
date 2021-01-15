/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.blockly;

import androidx.annotation.IntDef;

import com.ubtedu.ukit.project.blockly.model.AudioItem;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author naOKi
 * @Date 2019/12/17
 **/
public interface AudioListPresenter {

    int CAN_DO_NOTHING = 0;
    int CAN_DO_PLAY = 1 << 0;
    int CAN_DO_STOP = 1 << 1;
    int CAN_DO_SAVE = 1 << 2;
    int CAN_DO_RENAME = 1 << 3;
    int CAN_DO_DELETE = 1 << 4;
    int CAN_DO_RECORD = 1 << 5;
    int CAN_DO_CHOICE = 1 << 6;
    int CAN_DO_ALL = 0xFFFFFFFF;

    @IntDef({CAN_DO_NOTHING,
            CAN_DO_PLAY,
            CAN_DO_STOP,
            CAN_DO_SAVE,
            CAN_DO_RENAME,
            CAN_DO_DELETE,
            CAN_DO_RECORD,
            CAN_DO_CHOICE,
            CAN_DO_ALL})
    @Retention(RetentionPolicy.SOURCE)
    @interface AllowFlag {

    }

    List<AudioItem> loadAudioList();
    void delete(AudioItem audio);
    void rename(AudioItem audio, String newName);
    void save(AudioItem audio);
    void play(AudioItem audio, IAudioPlayListener listener);
    void stop(boolean isBackground);

    String getDefaultAudioName(ArrayList<AudioItem> list);
    boolean canAddNew(ArrayList<AudioItem> list);

    boolean canDoAction(@AllowFlag int actionFlag);

    void init();
    void release();

    interface IAudioPlayListener {
        void onFinished();
        void onError();
    }

    abstract class IAudioPlayListenerV2 implements IAudioPlayListener {

        private String tag;

        public IAudioPlayListenerV2(String tag) {
            this.tag = tag;
        }

        public abstract void onFinished(String tag);
        public abstract void onError(String tag);

        @Deprecated
        @Override
        public final void onFinished() {
            onFinished(tag);
        }

        @Deprecated
        @Override
        public final void onError() {
            onError(tag);
        }

    }

}
