/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.blockly;


import android.text.TextUtils;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.project.Workspace;
import com.ubtedu.ukit.project.blockly.audio.AudioPlayer;
import com.ubtedu.ukit.project.blockly.model.AudioItem;
import com.ubtedu.ukit.project.blockly.model.LocalAudioItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author naOKi
 * @Date 2019/12/17
 **/
public class LocalAudioListPresenter implements AudioListPresenter {

    private AudioListUI mUI;

    public LocalAudioListPresenter(AudioListUI ui) {
        mUI = ui;
    }

    @Override
    public List<AudioItem> loadAudioList() {
        mUI.changeUIStatus(AudioListUI.UIStatus.SUCCESS);
        ArrayList<AudioItem> items = new ArrayList<>();
        if (Workspace.getInstance().getProject() != null && Workspace.getInstance().getProject().audioList != null) {
            List<BlocklyAudio> audioList = Workspace.getInstance().getProject().audioList;
            for(BlocklyAudio audio : audioList) {
                items.add(new LocalAudioItem(audio));
            }
        }
        return items;
    }

    @Override
    public void delete(AudioItem audio) {
        if(audio instanceof LocalAudioItem) {
            Workspace.getInstance().removeProjectFile(((LocalAudioItem)audio).getBlocklyAudio());
        }
    }

    @Override
    public void rename(AudioItem audio, String newName) {
        if(audio instanceof LocalAudioItem) {
            audio.setName(newName);
            Workspace.getInstance().renameProjectFile(((LocalAudioItem)audio).getBlocklyAudio(), newName);
        }
    }

    @Override
    public void save(AudioItem audio) {
        if(audio instanceof LocalAudioItem) {
            Workspace.getInstance().saveProjectFile(((LocalAudioItem)audio).getBlocklyAudio());
        }
    }

    @Override
    public void play(AudioItem audio, IAudioPlayListener listener) {
        AudioPlayer.getInstance().play(audio.getFilePath(), new LocalProgressListener(listener));
    }

    @Override
    public void stop(boolean isBackground) {
        AudioPlayer.getInstance().stop();
    }

    @Override
    public String getDefaultAudioName(ArrayList<AudioItem> list) {
        int num = 1;
        String prefixName = UKitApplication.getInstance().getString(R.string.audio_record_template_name);
        if (Workspace.getInstance().getProject() != null && Workspace.getInstance().getProject().audioList != null) {
            List<BlocklyAudio> audioList = Workspace.getInstance().getProject().audioList;
            for (int i = 0; i < audioList.size(); i++) {
                String targetName = prefixName + num;
                boolean found = false;
                for (BlocklyAudio temp : audioList) {
                    if (TextUtils.equals(targetName, temp.name)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    break;
                }
                num++;
            }
        }
        String audioName = prefixName + num;
        return audioName;
    }

    @Override
    public boolean canAddNew(ArrayList<AudioItem> list) {
        return true;
    }

    @Override
    public boolean canDoAction(@AllowFlag int actionFlag) {
        return true;
    }

    @Override
    public void init() {

    }

    @Override
    public void release() {

    }

    private class LocalProgressListener implements AudioPlayer.IProgressListener {

        private IAudioPlayListener listener;

        public LocalProgressListener(IAudioPlayListener listener) {
            this.listener = listener;
        }

        @Override
        public void onFinished() {
            if(listener != null) {
                listener.onFinished();
            }
        }

        @Override
        public void onError() {
            if(listener != null) {
                listener.onError();
            }
        }

    }

}
