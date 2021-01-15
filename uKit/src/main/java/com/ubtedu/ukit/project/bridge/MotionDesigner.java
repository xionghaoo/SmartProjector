/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.bridge;

import com.ubtedu.bridge.BridgeArray;
import com.ubtedu.bridge.BridgeBoolean;
import com.ubtedu.bridge.BridgeObject;
import com.ubtedu.bridge.BridgeResult;
import com.ubtedu.bridge.OnCallback;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.error.BluetoothCommonErrorHelper;
import com.ubtedu.ukit.bluetooth.processor.PeripheralErrorCollector;
import com.ubtedu.ukit.menu.settings.Settings;

import java.util.Timer;

/**
 * @Author qinicy
 * @Date 2018/12/5
 **/
public class MotionDesigner {

    private Timer mTimer;

    private static MotionDesigner instance;

    private boolean isRecording = false;

    private boolean chargingFlag = false;
    private boolean needShowChargingProtection = false;

    private MotionDesigner() {
    }

    public static MotionDesigner getInstance() {
        synchronized (MotionDesigner.class) {
            if(instance == null) {
                instance = new MotionDesigner();
            }
            return instance;
        }
    }

    public void reset() {
        stopAutoProgram();
        setRecording(false);
    }

    public void pause() {
        stopAutoProgram();
    }

    public void resume() {
        stopAutoProgram();
        synchronized (MotionDesigner.class) {
            if(isRecording) {
                PeripheralErrorCollector.getInstance().updateCollectorState(PeripheralErrorCollector.CollectorState.START, PeripheralErrorCollector.ErrorCollectorType.MOTION_RECORDING);
            }
        }
    }

    public void setRecording(boolean recording) {
        synchronized (MotionDesigner.class) {
            isRecording = recording;
        }
    }

    public void servoPowerOff(BridgeArray servoIdsJson, OnCallback callback) {
//        if(!BluetoothHelper.isConnected()) {
//            BluetoothCommonErrorActivity.openBluetoothCommonErrorActivity(BluetoothCommonErrorActivity.CommonError.BLUETOOTH_NOT_CONNECTED);
//            callback.onCallback(BridgeResult.FAIL());
//            return;
//        }
        BluetoothCommunicator.getInstance().servoPowerOff(servoIdsJson.toString(), callback);
    }

    public void singleProgram(BridgeArray servoIdsJson, OnCallback callback) {
//        if(!BluetoothHelper.isConnected()) {
//            BluetoothCommonErrorActivity.openBluetoothCommonErrorActivity(BluetoothCommonErrorActivity.CommonError.BLUETOOTH_NOT_CONNECTED);
//            callback.onCallback(BridgeResult.FAIL());
//            return;
//        }
        BluetoothCommunicator.getInstance().angleFeedback(true, servoIdsJson.toString(), callback);
    }

    public void startAutoProgram(final BridgeArray servoIdsJson, Number internal, final OnCallback callback) {
//        stopAutoProgram();
//        mTimer = new Timer();
//
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                BluetoothCommunicator.getInstance().angleFeedback(false, servoIdsJson.toString(), callback);
//            }
//        };
//        mTimer.schedule(task, internal.intValue(), internal.intValue());
        if(callback != null) {
            BridgeResult bridgeResult = BridgeResult.FAIL();
            bridgeResult.complete = BridgeBoolean.wrap(true);
            callback.onCallback(bridgeResult);
        }
    }

    public BridgeResult stopAutoProgram() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
        return BridgeResult.SUCCESS();
    }

    public void execMotion(BridgeObject args, OnCallback callback) {
//        if(!BluetoothHelper.isConnected()) {
//            BluetoothCommonErrorActivity.openBluetoothCommonErrorActivity(R.string.bluetooth_not_connect_title, R.string.bluetooth_not_connect_msg);
//            callback.onCallback(BridgeResult.FAIL());
//            return;
//        }
        if(PeripheralErrorCollector.getInstance().isCurrentType(PeripheralErrorCollector.ErrorCollectorType.MOTION_PREVIEW)) {
            if (Settings.isChargingProtection() && BluetoothHelper.isCharging()) {
                if (callback != null) {
                    callback.onCallback(BridgeResult.FAIL());
                }
                return;
            }
        } else {
            synchronized (this) {
                if (Settings.isChargingProtection() && chargingFlag) {
                    if (needShowChargingProtection) {
                        needShowChargingProtection = false;
                        BluetoothCommonErrorHelper.openBluetoothCommonErrorActivity(BluetoothCommonErrorHelper.CommonError.CHARGING_PROTECTION);
                    }
                    if (callback != null) {
                        callback.onCallback(BridgeResult.FAIL());
                    }
                    return;
                }
            }
        }
        BluetoothCommunicator.getInstance().execMotionFrame(args.toString(), callback);
    }

    public void updateChargingFlag(boolean isCharging) {
        synchronized (this) {
            if(isCharging) {
                if(!chargingFlag) {
                    needShowChargingProtection = true;
                    chargingFlag = true;
                }
            } else {
                chargingFlag = false;
                needShowChargingProtection = false;
            }
        }
    }

    public void resetShowChargingProtectionFlag() {
        synchronized (this) {
            if(chargingFlag) {
                needShowChargingProtection = true;
            }
        }
    }

}
