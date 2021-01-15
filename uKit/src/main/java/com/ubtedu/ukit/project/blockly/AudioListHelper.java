package com.ubtedu.ukit.project.blockly;


import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.ubtedu.alpha1x.core.base.fragment.OnDialogFragmentDismissListener;
import com.ubtedu.bridge.APICallback;
import com.ubtedu.bridge.BridgeBoolean;
import com.ubtedu.bridge.BridgeObject;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoAudioRecord;
import com.ubtedu.ukit.project.blockly.model.LocalAudioItem;
import com.ubtedu.ukit.project.blockly.model.UKitAudioItem;
import com.ubtedu.ukit.project.bridge.api.ActivityHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author qinicy
 * @Date 2019/4/17
 **/
public class AudioListHelper {

    private static AppCompatActivity getActivity() {
        return ActivityHelper.getResumeActivity();
    }

    private static void runOnMainThread(Runnable runnable) {
        if(Looper.getMainLooper() == Looper.myLooper()) {
            runnable.run();
        } else {
            if (getActivity() == null) {
                return;
            }
            getActivity().runOnUiThread(runnable);
        }
    }

    public static void openUKitAudioList(final String currentAudioId, final APICallback<BridgeObject> callback) {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() == null) {
                    return;
                }
                AudioListDialogFragment fragment = UKitAudioListDialogFragment.newInstance(currentAudioId);
                fragment.setDismissListener(new OnDialogFragmentDismissListener() {
                    @Override
                    public void onDismiss(Object... value) {
                        Map<Object, Object> result = new HashMap<>();
                        boolean isSelect = value != null && value.length > 0 && value[0] instanceof UKitAudioItem;
                        result.put(BlocklyConstants.IS_SELECT, BridgeBoolean.wrap(true));
                        if (isSelect) {
                            URoAudioRecord audioRecord = ((UKitAudioItem)value[0]).getAudioRecord();
                            result.put(BlocklyConstants.AUDIO_ID, audioRecord.getName());
                            result.put(BlocklyConstants.AUDIO_NAME, audioRecord.getName());
                        } else {
                            result.put(BlocklyConstants.AUDIO_ID, "");
                            result.put(BlocklyConstants.AUDIO_NAME, "");
                        }
                        callback.onCallback(new BridgeObject(result), true);
                    }
                });
                fragment.show(getActivity().getSupportFragmentManager(), "AudioListDialogFragment");
            }
        });
    }

    public static void openLocalAudioList(final String currentAudioId, final APICallback<BridgeObject> callback) {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() == null) {
                    return;
                }
                AudioListDialogFragment fragment = LocalAudioListDialogFragment.newInstance(currentAudioId);
                fragment.setDismissListener(new OnDialogFragmentDismissListener() {
                    @Override
                    public void onDismiss(Object... value) {
                        Map<Object, Object> result = new HashMap<>();
                        boolean isSelect = value != null && value.length > 0 && value[0] instanceof LocalAudioItem;
                        result.put(BlocklyConstants.IS_SELECT, BridgeBoolean.wrap(true));
                        if (isSelect) {
                            BlocklyAudio audio = ((LocalAudioItem)value[0]).getBlocklyAudio();
                            result.put(BlocklyConstants.AUDIO_ID, audio.id);
                            result.put(BlocklyConstants.AUDIO_NAME, audio.name);
                        } else {
                            result.put(BlocklyConstants.AUDIO_ID, "");
                            result.put(BlocklyConstants.AUDIO_NAME, "");
                        }
                        callback.onCallback(new BridgeObject(result), true);
                    }
                });
                fragment.show(getActivity().getSupportFragmentManager(), "AudioListDialogFragment");
            }
        });
    }
}
   