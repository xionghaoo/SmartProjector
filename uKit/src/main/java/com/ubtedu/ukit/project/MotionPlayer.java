package com.ubtedu.ukit.project;

import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoMissionCallback;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocationSequence;
import com.ubtedu.deviceconnect.libs.base.invocation.URoMissionAbortSignal;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.BtInvocationFactory;
import com.ubtedu.ukit.menu.settings.Settings;
import com.ubtedu.ukit.project.bridge.api.ToastHelper;
import com.ubtedu.ukit.project.vo.Motion;

public class MotionPlayer {

    //    private BluetoothActionSequence mMotionActionSequence;
    private URoMissionAbortSignal sequenceAbortSignal;
    private OnMotionPlayStateChangeListener mListener;

    private String mPlayMotionId = null;

    public void setListener(OnMotionPlayStateChangeListener listener) {
        this.mListener = listener;
    }

    public void playMotion(Motion motion) {
        if (Settings.isChargingProtection() && BluetoothHelper.isCharging()) {
            showToast(R.string.project_controller_motion_charging_protect_msg);
            return;
        }
        if (!BluetoothHelper.isBluetoothConnected()) {
            showToast(R.string.motion_bluetooth_not_connect_msg);
            return;
        }
        if (sequenceAbortSignal != null && !sequenceAbortSignal.isAbort()) {
            sequenceAbortSignal.abort();
        }
        URoInvocationSequence motionActionSequence = BtInvocationFactory.exeMotion(motion);


        if (motion != null) {
            mPlayMotionId = motion.id;
        }
        motionActionSequence.setCompletionCallback(new URoMissionCallback() {
            @Override
            public void onMissionNextStep(int currentStep, int totalStep) {
                if (Settings.isChargingProtection() && BluetoothHelper.isCharging()) {
                    showToast(R.string.project_controller_motion_charging_protect_msg);
                    stopMotion();
                }
            }

            @Override
            public void onMissionBegin() {

            }

            @Override
            public void onMissionEnd() {
                sequenceAbortSignal = null;
            }

            @Override
            public void onMissionAccept(URoMissionAbortSignal abortSignal) {
                sequenceAbortSignal = abortSignal;
            }

            @Override
            public void onComplete(URoCompletionResult result) {
                if (!result.isSuccess()) {
                    showToast(R.string.motion_peripheral_error_msg);
                }
                stopMotion();
            }
        });

        if (Settings.isChargingProtection() && BluetoothHelper.isCharging()) {
            showToast(R.string.project_controller_motion_charging_protect_msg);
            stopMotion();
        }
        BluetoothHelper.addCommand(motionActionSequence);
//        mMotionActionSequence.listener(new IActionSequenceProcessListener() {
//            @Override
//            public void onProcessChanged(BluetoothActionSequence bas, int currentIndex, int totalAction) {
//                if (Settings.isChargingProtection() && BtInfoHolder.getBatteryInfoData(false) != null && BtInfoHolder.getBatteryInfoData().charging) {
//                    showToast(R.string.project_controller_motion_charging_protect_msg);
//                    stopMotion();
//                }
//            }
//
//            @Override
//            public void onProcessBegin(BluetoothActionSequence bas) {
//            }
//
//            @Override
//            public void onProcessEnd(BluetoothActionSequence bas) {
//            }
//        });

        if (mListener != null) {
            mListener.onMotionPlayStateChanged();
        }
    }

    public void stopMotion() {
        mPlayMotionId = null;
        if (sequenceAbortSignal != null && !sequenceAbortSignal.isAbort()) {
            sequenceAbortSignal.abort();
        }
        if (mListener != null) {
            mListener.onMotionPlayStateChanged();
        }
    }

    public String getPlayMotionId() {
        return mPlayMotionId;
    }

    private void showToast(int resId) {
        ToastHelper.toastShort((UKitApplication.getInstance().getString(resId)));
    }

    public interface OnMotionPlayStateChangeListener {
        void onMotionPlayStateChanged();
    }
}
