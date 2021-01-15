/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.blockly;


import com.ubtedu.alpha1x.utils.LogUtil;
import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoConnectStatusChangeListener;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoPushMessageReceivedListener;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocationSequence;
import com.ubtedu.deviceconnect.libs.base.model.event.URoPushMessageData;
import com.ubtedu.deviceconnect.libs.base.model.event.URoPushMessageType;
import com.ubtedu.deviceconnect.libs.base.product.URoConnectStatus;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoAudioRecord;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoAudioRecordState;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.BtInvocationFactory;
import com.ubtedu.ukit.bluetooth.IUKitCommandResponse;
import com.ubtedu.ukit.bluetooth.UkitInvocation;
import com.ubtedu.ukit.project.blockly.model.AudioItem;
import com.ubtedu.ukit.project.blockly.model.UKitAudioItem;

import java.util.Random;

/**
 * @Author naOKi
 * @Date 2019/12/17
 **/
public class UKitAudioRecordPresenter extends AudioRecordPresenter implements URoPushMessageReceivedListener, URoConnectStatusChangeListener {

    public static final String TMP_AUDIO_RECORD_NAME = "tmp";

    private boolean isRecording;
    private int sessionId = 0;

    private Random r = new Random();

    public UKitAudioRecordPresenter(AudioRecordUI ui) {
        super(ui);
    }

    @Override
    public AudioItem newAudioImpl() {
        URoAudioRecord audioRecord = new URoAudioRecord(TMP_AUDIO_RECORD_NAME, 0, 0, null);
        return new UKitAudioItem(audioRecord);
    }

    @Override
    public boolean isRecordingImpl() {
        return isRecording;
    }

    @Override
    public boolean startRecordAudioImpl() {
        String name = getAudio().getName();
        sessionId = r.nextInt();
        URoLogUtils.d("startRecordAudio: %08x", sessionId);
        URoInvocationSequence sequence=new URoInvocationSequence();
        sequence.action(BtInvocationFactory.stopExecScript(null));
        UkitInvocation audioInvocation = BtInvocationFactory.startAudioRecord(sessionId, name, RECORD_TIME_MAX, null);
        audioInvocation.setTimeoutThreshold(RECORD_TIMEOUT_MAX);
        sequence.action(audioInvocation);
        sequence.setCompletionCallback(new IUKitCommandResponse<Void>() {
            @Override
            protected void onUKitCommandResponse(URoCompletionResult<Void> result) {
                isRecording = result.isSuccess();
                if (!result.isSuccess()) {
                    sessionId = 0;
                    notifyRecordFinish(false, getString(R.string.audio_start_record_failed_msg), null);
                }
            }
        });
        return BluetoothHelper.addCommand(sequence);
    }

    @Override
    public void stopRecordAudioImpl() {
        UkitInvocation invocation = BtInvocationFactory.stopAudioRecord(new IUKitCommandResponse<Void>() {
            @Override
            protected void onUKitCommandResponse(URoCompletionResult<Void> result) {
                isRecording = false;
                if (!result.isSuccess()) {
                    notifyRecordFinish(false, getString(R.string.audio_stop_record_failed_msg), null);
                }
            }
        });
        BluetoothHelper.addCommand(invocation);
    }

    @Override
    public void startRecordAudio() {
        if (cancelFlag){
            return;
        }
        super.startRecordAudio();
    }

    @Override
    public void stopRecordAudio() {
        if (cancelFlag){
            return;
        }
        if (getUI() != null) {
            getUI().onRecordProcessing();
        }
        BluetoothHelper.getBtHandler().postDelayed(mTimeoutRunnable, 40000L);
        super.stopRecordAudio();
    }

    @Override
    public void cancelImpl() {
        if (isRecording) {
            UkitInvocation invocation = BtInvocationFactory.cancelAudioRecord(new IUKitCommandResponse<Void>() {
                @Override
                protected void onUKitCommandResponse(URoCompletionResult<Void> result) {
                    BluetoothHelper.getBtHandler().removeCallbacks(mTimeoutRunnable);
                    notifyRecordFinish(false, null, null);
                    isRecording = false;
                }
            });
            BluetoothHelper.addCommand(invocation);
        }
    }

    @Override
    protected void initImpl() {
        BluetoothHelper.addPushMessageReceivedListener(this);
        BluetoothHelper.addConnectStatusChangeListener(this);
    }

    @Override
    public void releaseImpl() {
        BluetoothHelper.removePushMessageReceivedListener(this);
        BluetoothHelper.removeConnectStatusChangeListener(this);
        BluetoothHelper.getBtHandler().removeCallbacks(mTimeoutRunnable);
    }

    private void deleteAudioRecord() {
        if (getAudio() instanceof UKitAudioItem) {
            UkitInvocation invocation = BtInvocationFactory.deleteAudioRecord(((UKitAudioItem) getAudio()).getAudioRecord(), null);
            BluetoothHelper.addCommand(invocation);
        }
    }

    @Override
    public void onConnectStatusChanged(URoProduct product, URoConnectStatus connectStatus) {
        if (URoConnectStatus.DISCONNECTED.equals(connectStatus)) {
            BluetoothHelper.getBtHandler().removeCallbacks(mTimeoutRunnable);
            notifyRecordFinish(false, null, null);
            isRecording = false;
        }
    }

    @Override
    public void onPushMessageReceived(URoProduct product, URoPushMessageType type, int subType, URoPushMessageData data) {
        if (URoPushMessageType.AUDIO_RECORD_REPORT.equals(type)) {
            URoAudioRecord audioRecord = (URoAudioRecord) data.getValue();
            LogUtil.d("URoAudioRecord: " + audioRecord);
            if(sessionId != 0 && audioRecord.getSessionId() != sessionId) {
                //不是当前录音session的结果，不做处理
                return;
            }
            if (URoAudioRecordState.COMPLETE.equals(audioRecord.getState())
                    || URoAudioRecordState.STOP_SUCCESS.equals(audioRecord.getState())) {
                if (getAudio() instanceof UKitAudioItem) {
                    URoAudioRecord currAudioRecord = ((UKitAudioItem) getAudio()).getAudioRecord();
                    currAudioRecord.setDuration(audioRecord.getDuration());
                    currAudioRecord.setDate(audioRecord.getDate());
                }
                BluetoothHelper.getBtHandler().removeCallbacks(mTimeoutRunnable);
                notifyRecordFinish(true, null, getAudio());
                isRecording = false;
                sessionId = 0;
            } else if (URoAudioRecordState.FAIL.equals(audioRecord.getState())) {
                BluetoothHelper.getBtHandler().removeCallbacks(mTimeoutRunnable);
                notifyRecordFinish(false, getString(R.string.audio_stop_record_failed_msg), null);
                isRecording = false;
                sessionId = 0;
            } else if (URoAudioRecordState.START_FAILURE.equals(audioRecord.getState())) {
                notifyRecordFinish(false, getString(R.string.audio_start_record_failed_msg), null);
                isRecording = false;
                sessionId = 0;
            } else if (URoAudioRecordState.START_SUCCESS.equals(audioRecord.getState())) {
                isRecording = true;
            } else if (URoAudioRecordState.STOP_FAILURE.equals(audioRecord.getState())) {
                BluetoothHelper.getBtHandler().removeCallbacks(mTimeoutRunnable);
                notifyRecordFinish(false, getString(R.string.audio_stop_record_failed_msg), null);
                isRecording = false;
            }
        }
    }

    private String getString(int resId) {
        return UKitApplication.getInstance().getString(resId);
    }

    @Override
    protected void notifyRecordFinish(boolean isSuccess, String errorMsg, AudioItem audioItem) {
        super.notifyRecordFinish(isSuccess, errorMsg, audioItem);
        //删除文件有可能会引起正在录音的文件也删除，这样会导致文件系统异常，因为写入的是临时文件，暂时可以先不删除
//        if(!isSuccess) {
//            deleteAudioRecord();
//        }
    }

    private Runnable mTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            isRecording = false;
            notifyRecordFinish(false, getString(R.string.audio_stop_record_failed_msg), null);
        }
    };
}
