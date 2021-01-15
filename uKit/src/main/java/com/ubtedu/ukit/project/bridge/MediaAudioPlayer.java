package com.ubtedu.ukit.project.bridge;

import com.ubtedu.bridge.APICallback;
import com.ubtedu.bridge.BridgeBoolean;
import com.ubtedu.bridge.BridgeObject;
import com.ubtedu.bridge.BridgeResult;
import com.ubtedu.bridge.OnCallback;
import com.ubtedu.ukit.common.files.FileHelper;
import com.ubtedu.ukit.project.Workspace;
import com.ubtedu.ukit.project.blockly.BlocklyAudio;
import com.ubtedu.ukit.project.blockly.BlocklySound;
import com.ubtedu.ukit.project.blockly.audio.AudioPlayer;

import org.json.JSONException;

import java.io.File;

/**
 * @Author naOKi
 * @Date 2018/12/21
 **/
public class MediaAudioPlayer {

    private MediaAudioPlayer() {
    }

    private static class SingletonHolder {
        private final static MediaAudioPlayer instance = new MediaAudioPlayer();
    }

    public static MediaAudioPlayer getInstance() {
        return MediaAudioPlayer.SingletonHolder.instance;
    }


    public void playAudio(BridgeObject jsonObject, final OnCallback callback) {
        try {
            BlocklySound sound = getSoundFromArguments(jsonObject);
            AudioPlayer.IProgressListener listener = null;
            if (sound.isDelay) {
                listener = new AudioPlayer.IProgressListener() {
                    @Override
                    public void onFinished() {
                        BridgeResult result = BridgeResult.SUCCESS();
                        result.data = BridgeBoolean.TRUE();
                        notifyPlayCallback(callback, result);
                    }

                    @Override
                    public void onError() {
                        BridgeResult result = BridgeResult.SUCCESS();
                        result.data = BridgeBoolean.FALSE();
                        notifyPlayCallback(callback, result);
                    }
                };
            }
            boolean playSuccess = AudioPlayer.getInstance().play(sound.path, listener);
            if (!playSuccess || !sound.isDelay) {
                BridgeResult result = BridgeResult.SUCCESS();
                result.data = BridgeBoolean.wrap(playSuccess);
                notifyPlayCallback(callback, result);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            notifyPlayCallback(callback, BridgeResult.ILLEGAL_ARGUMENTS().msg(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            notifyPlayCallback(callback, BridgeResult.FAIL().msg(e.getMessage()));
        }
    }

    public void stopAudio() {
        AudioPlayer.getInstance().stop();
    }

    private BlocklySound getSoundFromArguments(BridgeObject object) throws JSONException {
        final BlocklySound sound = new BlocklySound();
        sound.type = object.getString("type");
        sound.key = object.getString("key");
        sound.isDelay = BridgeBoolean.isTrue(object.optInt("isdelay"));
        handleSoundPath(sound);
        return sound;
    }

    private void handleSoundPath(BlocklySound sound) {
        if (BlocklySound.TYPE_RECORDING.equals(sound.type)) {
            //录音文件,key是录音文件的id
            sound.path = FileHelper.join(Workspace.AUDIO_DIR, sound.key + BlocklyAudio.AAC_SUFFIX);
        } else {
            //Blockly内置的音频文件
            String path = FileHelper.join(BlocklySound.SAMPLE_SOUND_PATH, sound.type, sound.key);
            File file = new File(path);
            if (file.exists()) {
                String check = "^.+(aac|wav|mp3|3gp)$";
                for (String name : file.list()) {
                    if (name.matches(check)) {
                        sound.path = file.getAbsolutePath() + File.separator + name;
                        break;
                    }
                }
            }
        }
    }

    private void notifyPlayCallback(OnCallback callback, BridgeResult result) {
        if (callback != null) {

            if (callback instanceof APICallback) {
                APICallback apiCallback = (APICallback) callback;
                apiCallback.onCallback(result.data, true);
            } else {
                callback.onCallback(result);
            }
        }
    }
}
