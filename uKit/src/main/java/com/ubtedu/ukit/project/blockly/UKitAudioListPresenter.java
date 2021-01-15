/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.blockly;


import android.text.TextUtils;

import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoConnectStatusChangeListener;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoPushMessageReceivedListener;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocationSequence;
import com.ubtedu.deviceconnect.libs.base.model.event.URoPushMessageData;
import com.ubtedu.deviceconnect.libs.base.model.event.URoPushMessageType;
import com.ubtedu.deviceconnect.libs.base.product.URoConnectStatus;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoAudioPlayResult;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoAudioPlayState;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoAudioRecord;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.BtInvocationFactory;
import com.ubtedu.ukit.bluetooth.IUKitCommandResponse;
import com.ubtedu.ukit.bluetooth.UkitInvocation;
import com.ubtedu.ukit.bluetooth.error.BluetoothCommonErrorHelper;
import com.ubtedu.ukit.project.blockly.model.AudioItem;
import com.ubtedu.ukit.project.blockly.model.UKitAudioItem;
import com.ubtedu.ukit.project.bridge.api.ToastHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.ubtedu.ukit.project.blockly.UKitAudioRecordPresenter.TMP_AUDIO_RECORD_NAME;

/**
 * @Author naOKi
 * @Date 2019/12/17
 **/
public class UKitAudioListPresenter implements AudioListPresenter, URoPushMessageReceivedListener, URoConnectStatusChangeListener {

    private static final int MAX_AUDIO_RECORD_NUM = 20;

    private AudioListUI mUI;

    private IAudioPlayListener listener;

    private final Object statusLock = new Object();
    private Status status = Status.INIT;

    private PlayRequest nextPlayRequest = null;
    private int sessionId = 0;

    private Random r = new Random();

    private enum Status {
        INIT, READY, STOPPING, PLAYING
    }

    public UKitAudioListPresenter(AudioListUI ui) {
        mUI = ui;
    }

    @Override
    public List<AudioItem> loadAudioList() {
        if(!BluetoothHelper.isConnected()) {
            BluetoothCommonErrorHelper.openBluetoothCommonErrorActivity(BluetoothCommonErrorHelper.CommonError.BLUETOOTH_NOT_CONNECTED);
            mUI.dismissUI();
            return null;
        }
        mUI.changeUIStatus(AudioListUI.UIStatus.LOADING);
        UkitInvocation invocation = BtInvocationFactory.getAudioRecordList(new IUKitCommandResponse<URoAudioRecord[]>() {
            @Override
            protected void onUKitCommandResponse(URoCompletionResult<URoAudioRecord[]> result) {
                if(result.isSuccess()) {
                    URoAudioRecord[] audioRecords = result.getData();
                    ArrayList<AudioItem> list = new ArrayList<>();
                    for(URoAudioRecord audioRecord : audioRecords) {
                        if(TextUtils.equals(audioRecord.getName(), TMP_AUDIO_RECORD_NAME)) {
                            continue;
                        }
                        list.add(new UKitAudioItem(audioRecord));
                    }
                    Collections.reverse(list);
                    mUI.updateAudioList(list);
                    mUI.changeUIStatus(AudioListUI.UIStatus.SUCCESS);
                } else {
                    mUI.changeUIStatus(AudioListUI.UIStatus.FAILURE);
                }
            }
        });
        BluetoothHelper.addCommand(invocation);
        return null;
    }

    @Override
    public void delete(AudioItem audio) {
        if(!BluetoothHelper.isConnected()) {
            return;
        }
        if(audio instanceof UKitAudioItem) {
            UkitInvocation invocation = BtInvocationFactory.deleteAudioRecord(((UKitAudioItem)audio).getAudioRecord(), new IUKitCommandResponse<Void>() {
                @Override
                protected void onUKitCommandResponse(URoCompletionResult<Void> result) {
                    if(!result.isSuccess()) {
                        showToast(R.string.audio_delete_failed_msg);
                    }
                }
            });
            BluetoothHelper.addCommand(invocation);
        }
    }

    @Override
    public void rename(AudioItem audio, String newName) {
        if(!BluetoothHelper.isConnected()) {
            return;
        }
        if(audio instanceof UKitAudioItem) {
            URoAudioRecord currRecord = ((UKitAudioItem)audio).getAudioRecord();
            URoAudioRecord cloneRecord = new URoAudioRecord(currRecord.getName(), currRecord.getDuration(), currRecord.getDate(), currRecord.getState());
            audio.setName(newName);
            UkitInvocation invocation = BtInvocationFactory.renameAudioRecord(cloneRecord, newName, new IUKitCommandResponse<Void>() {
                @Override
                protected void onUKitCommandResponse(URoCompletionResult<Void> result) {
                    if(!result.isSuccess()) {
                        showToast(R.string.audio_rename_failed_msg);
                    }
                }
            });
            BluetoothHelper.addCommand(invocation);
        }
    }

    @Override
    public void save(AudioItem audio) {
    }

    private boolean updateStatus(Status newStatus, Object... params) {
        synchronized (statusLock) {
            boolean result = false;
            if(newStatus == null || status.equals(newStatus)) {
                return result;
            }
            Status oldStatus = status;
            switch (newStatus) {
            case INIT:
                //不需要处理，状态机应该不可能回滚到INIT状态
                break;

            case READY:
                status = newStatus;
                result = true;
                //恢复READY状态，检测是否需要开始下一个播放
                if(nextPlayRequest != null) {
                    PlayRequest playRequest = nextPlayRequest;
                    nextPlayRequest = null;
                    playRequest.run();
                }
                break;

            case PLAYING:
                if(oldStatus.equals(Status.READY)) {
                    result = true;
                    if(params.length > 0) {
                        LogUtil.d("开始播放: " + params[0]);
                    }
                    status = newStatus;
                }
                break;

            case STOPPING:
                if(!oldStatus.equals(Status.READY) && !oldStatus.equals(Status.STOPPING)) {
                    result = true;
                    LogUtil.d("正在停止");
                    status = newStatus;
                }
                break;
            }
            return result;
        }
    }

    @Override
    public void play(AudioItem audio, IAudioPlayListener listener) {
        if(!BluetoothHelper.isConnected()) {
            return;
        }
        if(audio instanceof UKitAudioItem) {
            PlayRequest playRequest = new PlayRequest(audio.getName(), listener);
            synchronized (statusLock) {
                sessionId = r.nextInt();
                //URoLogUtils.d("play: %08x", sessionId);
                if(Status.READY.equals(status)) {
                    playRequest.run();
                } else {
                    nextPlayRequest = playRequest;
                    if (!Status.STOPPING.equals(status)){
                        realStop(true);
                    }
                    updateStatus(Status.STOPPING, Boolean.TRUE);
                }
            }
        }
    }

    @Override
    public void stop(boolean isBackground) {
        stop(isBackground, true);
    }

    private void stop(boolean isBackground, boolean updateState) {
        if(!BluetoothHelper.isConnected()) {
            return;
        }
        if(updateState) {
            synchronized (statusLock) {
                if (Status.READY.equals(status) || Status.STOPPING.equals(status)) {
                    return;
                }
                updateStatus(Status.STOPPING, isBackground);
            }
        }
        realStop(isBackground);
    }

    private void realStop(boolean isBackground){
    UkitInvocation invocation = BtInvocationFactory.stopAudioPlay(new IUKitCommandResponse<Void>() {
        @Override
        protected void onUKitCommandResponse(URoCompletionResult<Void> result) {
            if(!isBackground && !result.isSuccess()) {
                showToast(R.string.audio_stop_play_failed_msg);
            }
        }
    });
    BluetoothHelper.addCommand(invocation);
}
    @Override
    public String getDefaultAudioName(ArrayList<AudioItem> list) {
        int num = 1;
        String prefixName = UKitApplication.getInstance().getString(R.string.audio_record_template_name);
        if(list != null) {
            for (int i = 0; i < list.size(); i++) {
                String targetName = prefixName + num;
                boolean found = false;
                for (AudioItem temp : list) {
                    if (TextUtils.equals(targetName, temp.getName())) {
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
        if(list == null || list.isEmpty()) {
            return true;
        }
        int count = 0;
        for(AudioItem item : list) {
            if(item instanceof UKitAudioItem) {
                count++;
            }
        }
        return count < MAX_AUDIO_RECORD_NUM;
    }

    @Override
    public boolean canDoAction(@AllowFlag int actionFlag) {
        if(!BluetoothHelper.isConnected()) {
            BluetoothCommonErrorHelper.openBluetoothCommonErrorActivity(BluetoothCommonErrorHelper.CommonError.BLUETOOTH_NOT_CONNECTED);
            mUI.dismissUI();
            return false;
        }
        return true;
    }

    @Override
    public void init() {
        BluetoothHelper.addPushMessageReceivedListener(this);
        BluetoothHelper.addConnectStatusChangeListener(this);
    }

    @Override
    public void release() {
        BluetoothHelper.removePushMessageReceivedListener(this);
        BluetoothHelper.removeConnectStatusChangeListener(this);
        listener = null;
    }
    
    @Override
    public void onConnectStatusChanged(URoProduct product, URoConnectStatus connectStatus) {
        if(listener != null) {
            listener.onError();
            listener = null;
        }
    }
    
    @Override
    public void onPushMessageReceived(URoProduct product, URoPushMessageType type, int subType, URoPushMessageData data) {
        if(URoPushMessageType.AUDIO_PLAY_REPORT.equals(type)) {
            URoAudioPlayResult result = (URoAudioPlayResult)data.getValue();
            LogUtil.d("URoAudioPlayResult: " + result);
            URoAudioPlayState state = result.getState();
            boolean currentSession = sessionId != 0 && result.getSessionId() == sessionId;
            switch (state) {
            case START_SUCCESS:
                updateStatus(Status.PLAYING);
                break;

            case STOP_FAILURE:
                break;

            case COMPLETE:
                if(currentSession) {
                    if (listener != null) {
                        listener.onFinished();
                        listener = null;
                    }
                    sessionId = 0;
                }
                updateStatus(Status.READY);
                break;
            case STOP_SUCCESS:
                if(currentSession) {
                    sessionId = 0;
                }
                updateStatus(Status.READY);
                break;

            case FAIL:
            case START_FAILURE:
                if(currentSession) {
                    if (listener != null) {
                        listener.onError();
                        listener = null;
                    }
                    sessionId = 0;
                }
                updateStatus(Status.READY);
                break;
            }
        }
    }
    
    private void showToast(int resId) {
        String msg = UKitApplication.getInstance().getString(resId);
        ToastHelper.toastShort(msg);
    }

    private class PlayRequest implements Runnable {

        private String name;
        private IAudioPlayListener listener;

        public PlayRequest(String name, IAudioPlayListener listener) {
            this.name = name;
            this.listener = listener;
        }

        @Override
        public void run() {
            if(!updateStatus(Status.PLAYING, name)) {
                return;
            }
            UKitAudioListPresenter.this.listener = listener;
            //URoLogUtils.d("startAudioPlay: %08x", sessionId);
            URoInvocationSequence sequence = new URoInvocationSequence();
            sequence.action(BtInvocationFactory.setAudioVolume(UKitAudioVolume.MEDIUM.getVolume(), null), true);
            sequence.action(BtInvocationFactory.startAudioPlay(sessionId, name, new IUKitCommandResponse<Void>() {
                @Override
                protected void onUKitCommandResponse(URoCompletionResult<Void> result) {
                    if(!result.isSuccess()) {
                        sessionId = 0;
                        showToast(R.string.audio_play_failed_msg);
                    }
                }
            }));
            BluetoothHelper.addCommand(sequence);
        }

    }

}
