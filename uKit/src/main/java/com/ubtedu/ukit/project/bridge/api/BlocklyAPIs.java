/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.project.bridge.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.ubtedu.bridge.APICallback;
import com.ubtedu.bridge.BaseBridgeAPI;
import com.ubtedu.bridge.BridgeArray;
import com.ubtedu.bridge.BridgeBoolean;
import com.ubtedu.bridge.BridgeObject;
import com.ubtedu.bridge.BridgeResult;
import com.ubtedu.bridge.BridgeResultCode;
import com.ubtedu.bridge.OnCallback;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoConnectStatusChangeListener;
import com.ubtedu.deviceconnect.libs.base.product.URoConnectStatus;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.BtInvocationFactory;
import com.ubtedu.ukit.bluetooth.UkitDeviceCompact;
import com.ubtedu.ukit.bluetooth.peripheral.setting.modifymode.PeripheralSettingModifyModeActivity;
import com.ubtedu.ukit.bluetooth.processor.CommonBoardNetworkStateHolder;
import com.ubtedu.ukit.bluetooth.processor.PeripheralErrorCollector;
import com.ubtedu.ukit.common.eventbus.ActivityOrientationEvent;
import com.ubtedu.ukit.common.eventbus.VisibilityEvent;
import com.ubtedu.ukit.menu.settings.Settings;
import com.ubtedu.ukit.project.Workspace;
import com.ubtedu.ukit.project.blockly.AudioListHelper;
import com.ubtedu.ukit.project.blockly.BlocklyAudio;
import com.ubtedu.ukit.project.blockly.BlocklyConstants;
import com.ubtedu.ukit.project.bridge.BluetoothCommunicator;
import com.ubtedu.ukit.project.bridge.BridgeCommunicator;
import com.ubtedu.ukit.project.bridge.DeviceDirectionHelper;
import com.ubtedu.ukit.project.bridge.MediaAudioPlayer;
import com.ubtedu.ukit.project.bridge.QueryAllSensorsTimer;
import com.ubtedu.ukit.project.bridge.arguments.PhoneStateArguments;
import com.ubtedu.ukit.project.bridge.functions.BlocklyFunctions;
import com.ubtedu.ukit.project.controller.manager.ControllerManager;
import com.ubtedu.ukit.project.vo.Motion;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Author qinicy
 * @Date 2018/12/4
 **/
public class BlocklyAPIs extends BaseBridgeAPI implements URoConnectStatusChangeListener {
    public final static String NAME_SPACE = "blockly";
    protected Handler mHandler;
    private DeviceDirectionHelper mDirectionHelper;
    private QueryAllSensorsTimer mQueryAllSensorsTimer;

    public BlocklyAPIs() {
        mHandler = new Handler(Looper.getMainLooper());
        BluetoothHelper.addConnectStatusChangeListener(this);
    }


    @JavascriptInterface
    public void registerPhoneStateObserver(final OnCallback callback) {
        if (mDirectionHelper == null) {
            mDirectionHelper = new DeviceDirectionHelper();
            mDirectionHelper.init(ActivityHelper.getResumeActivity());
        }
        mDirectionHelper.startDetect(callback);
        EventBus.getDefault().post(new ActivityOrientationEvent(true));
    }

    @JavascriptInterface
    public BridgeResult unRegisterPhoneStateObserver() {
        if (mDirectionHelper != null) {
            mDirectionHelper.stopDetect();
        }
        mDirectionHelper = null;
        EventBus.getDefault().post(new ActivityOrientationEvent(false));
        BridgeResult result = BridgeResult.SUCCESS();
        result.data = BridgeBoolean.TRUE();
        return result;
    }

    @JavascriptInterface
    public void startQueryAllSensorsTimer(final OnCallback callback) {
        if (mQueryAllSensorsTimer != null) {
            mQueryAllSensorsTimer.stop();
            mQueryAllSensorsTimer = null;
        }
        mQueryAllSensorsTimer = QueryAllSensorsTimer.getInstance();
        mQueryAllSensorsTimer.stop();
        mQueryAllSensorsTimer.setCallback(callback);
        mQueryAllSensorsTimer.start();
    }

    @JavascriptInterface
    public void stopQueryAllSensorsTimer(final OnCallback callback) {
        if (mQueryAllSensorsTimer != null) {
            mQueryAllSensorsTimer.stop();
            mQueryAllSensorsTimer = null;
        }
        BridgeResult result = BridgeResult.SUCCESS();
        result.data = BridgeBoolean.TRUE();
        callback.onCallback(result);
    }

    /**
     * 设置舵机角度+时长
     */
    @JavascriptInterface
    public void setServo(BridgeObject args, OnCallback callback) {
        BluetoothCommunicator.getInstance().setServo(args.toString(), callback);
    }

    /**
     * 设置舵机角度+速度
     */
    @JavascriptInterface
    public void setServobySpeed(BridgeArray args, OnCallback callback) {
        BluetoothCommunicator.getInstance().setServobySpeed(args.toString(), callback);
    }

    /**
     * 设置舵机角度+速度百分比
     */
    @JavascriptInterface
    public void setServoBySpeedPercent(BridgeArray args, OnCallback callback) {
        BluetoothCommunicator.getInstance().setServoBySpeedPercent(args.toString(), callback);
    }

    /**
     * 跳转至外设设置界面
     */
    @JavascriptInterface
    public BridgeResult redirectToPeripheral() {
        Context context = UKitApplication.getInstance();
        PeripheralSettingModifyModeActivity.openSetting(context);
        BridgeResult result = BridgeResult.SUCCESS();
        result.data = BridgeBoolean.TRUE();
        return result;
    }

    /**
     * 读取角模式舵机数据
     */
    @JavascriptInterface
    public void getPosture(OnCallback callback) {
        BluetoothCommunicator.getInstance().getPosture(callback);
    }

    /**
     * 设置电机速度
     */
    @JavascriptInterface
    public void setMotorSpeed(BridgeArray args, OnCallback callback) {
        BluetoothCommunicator.getInstance().setMotorSpeed(args.toString(), callback);
    }

    /**
     * 停止电机
     */
    @JavascriptInterface
    public void stopMotor(BridgeObject args, OnCallback callback) {
        BluetoothCommunicator.getInstance().stopMotor(args.toString(), callback);
    }

    /**
     * 设置眼灯-表情
     */
    @JavascriptInterface
    public void setEmoji(BridgeObject args, OnCallback callback) {
        BluetoothCommunicator.getInstance().setEmoji(args.toString(), callback);
    }

    /**
     * 设置眼灯-表情
     */
    @JavascriptInterface
    public void setScenelight(BridgeObject args, OnCallback callback) {
        BluetoothCommunicator.getInstance().setScenelight(args.toString(), callback);
    }

    /**
     * 设置眼灯-灯光
     */
    @JavascriptInterface
    public void setLEDs(BridgeObject args, OnCallback callback) {
        BluetoothCommunicator.getInstance().setLEDs(args.toString(), callback);
    }

    /**
     * 关闭所有灯光
     */
    @Deprecated
    @JavascriptInterface
    public void turnOffLeds(OnCallback callback) {
        BluetoothCommunicator.getInstance().turnOffLeds(callback);
    }

    /**
     * 设置超声灯光
     */
    @Deprecated
    @JavascriptInterface
    public void setUltrasonicLED(BridgeObject args, OnCallback callback) {
        BluetoothCommunicator.getInstance().setUltrasonicLED(args.toString(), callback);
    }

    @JavascriptInterface
    public void setGroupUltrasonicLED(BridgeObject args, OnCallback callback) {
        BluetoothCommunicator.getInstance().setGroupUltrasonicLED(args.toString(), callback);
    }

    /**
     * 设置灯带颜色
     * @param args
     * @param callback
     */
    @JavascriptInterface
    public void setLEDStripColor(BridgeObject args, OnCallback callback) {
        BluetoothCommunicator.getInstance().setLEDStripColor(args.toString(), callback);
    }

    /**
     * 设置灯带情景灯
     * @param args
     * @param callback
     */
    @JavascriptInterface
    public void setLEDStripExpression(BridgeObject args, OnCallback callback) {
        BluetoothCommunicator.getInstance().setLEDStripExpression(args.toString(), callback);
    }

    /**
     * 设置灯带亮度
     * @param args
     * @param callback
     */
    @JavascriptInterface
    public void setLEDStripBrightness(BridgeObject args, OnCallback callback) {
        BluetoothCommunicator.getInstance().setLEDStripBrightness(args.toString(), callback);
    }

    /**
     * 灯带灯光移动
     * @param args
     * @param callback
     */
    @JavascriptInterface
    public void moveLEDStrip(BridgeObject args, OnCallback callback) {
        BluetoothCommunicator.getInstance().moveLEDStrip(args.toString(), callback);
    }

    /**
     * 关闭灯带
     * @param args
     * @param callback
     */
    @JavascriptInterface
    public void closeLEDStrip(BridgeObject args, OnCallback callback) {
        BluetoothCommunicator.getInstance().closeLEDStrip(args.toString(), callback);
    }

    /**
     * 获取动作列表
     */
    @JavascriptInterface
    public BridgeResult getMotionList() {
        ArrayList<Map<String, Object>> resultData = new ArrayList<>();
        if (Workspace.getInstance() != null
                && Workspace.getInstance().getProject() != null
                && Workspace.getInstance().getProject().motionList != null) {
            List<Motion> motionList = Workspace.getInstance().getProject().motionList;
            for (Motion motion : motionList) {
                HashMap<String, Object> data = new HashMap<>();
                data.put("id", motion.id);
                data.put("name", motion.name);
                data.put("totalTime", motion.totaltime);
                resultData.add(data);
            }
        }
        BridgeResult result = BridgeResult.SUCCESS();
        result.data = resultData;
        return result;
    }

    /**
     * 执行指定动作
     */
    @JavascriptInterface
    public void execMotionFile(String id, OnCallback callback) {
        BluetoothCommunicator.getInstance().execMotion(id, callback);
    }

    /**
     * 开始运行
     */
    @JavascriptInterface
    public void startRobot(final OnCallback callback) {
        if (!BluetoothHelper.isConnected()) {
//            BluetoothCommonErrorActivity.openBluetoothCommonErrorActivity(R.string.bluetooth_not_connect_title, R.string.bluetooth_not_connect_msg);
            callback.onCallback(BridgeResult.FAIL());
            return;
        }
        BridgeResult result = BridgeResult.SUCCESS();
        result.data = BridgeBoolean.TRUE();
        callback.onCallback(result);
        EventBus.getDefault().post(new VisibilityEvent(false));
        PeripheralErrorCollector.getInstance().updateCollectorState(PeripheralErrorCollector.CollectorState.START, PeripheralErrorCollector.ErrorCollectorType.BLOCKLY);
    }

    /**
     * 停止运行
     */
    @JavascriptInterface
    public void stopRobot(final OnCallback callback) {
        EventBus.getDefault().post(new VisibilityEvent(true));
        if (mQueryAllSensorsTimer != null) {
            mQueryAllSensorsTimer.stop();
            mQueryAllSensorsTimer = null;
        }
        BluetoothCommunicator.getInstance().stopAllActionSequence(callback);
        PeripheralErrorCollector.getInstance().updateCollectorState(PeripheralErrorCollector.CollectorState.STOP, null);
    }

    /**
     * 开始运行脚本
     */
    @JavascriptInterface
    public void startPython(BridgeObject args, final OnCallback callback) {
        if (!BluetoothHelper.isConnected()) {
            callback.onCallback(BridgeResult.FAIL());
            return;
        }
        EventBus.getDefault().post(new VisibilityEvent(false));
        PeripheralErrorCollector.getInstance().updateCollectorState(PeripheralErrorCollector.CollectorState.START, PeripheralErrorCollector.ErrorCollectorType.BLOCKLY);
        BluetoothCommunicator.getInstance().startPython(args.toString(), new BridgeCallbackWarp(callback) {
            @Override
            protected void onCallbackInternal(BridgeResult result) {
                if(result.code == BridgeResultCode.SUCCESS) {
                    registerPhoneStateObserver(new OnCallback() {
                        private String phoneState = null;
                        @Override
                        public void onCallback(BridgeResult result) {
                            PhoneStateArguments arguments = (PhoneStateArguments)result.data;
                            if(arguments != null && arguments.state != null && (phoneState == null || !TextUtils.equals(phoneState, arguments.state))) {
                                phoneState = arguments.state;
                                BluetoothHelper.addCommand(BtInvocationFactory.updateScriptValue("ps", phoneState, null));
                            }
                        }
                    });
                } else {
                    EventBus.getDefault().post(new VisibilityEvent(true));
                    PeripheralErrorCollector.getInstance().updateCollectorState(PeripheralErrorCollector.CollectorState.STOP, null);
                }
            }
        });
    }

    @JavascriptInterface
    public void uploadPython(BridgeObject args, final OnCallback callback) {
        if (!BluetoothHelper.isConnected()) {
            callback.onCallback(BridgeResult.FAIL());
            return;
        }
        BluetoothCommunicator.getInstance().uploadOfflinePython(args.toString(), new BridgeCallbackWarp(callback) {
            @Override
            protected void onCallbackInternal(BridgeResult result) {
            }
        });
    }

    /**
     * 停止运行脚本
     */
    @JavascriptInterface
    public void stopPython(BridgeObject args,final OnCallback callback) {
        EventBus.getDefault().post(new VisibilityEvent(true));
        PeripheralErrorCollector.getInstance().updateCollectorState(PeripheralErrorCollector.CollectorState.STOP, null);
        BluetoothCommunicator.getInstance().stopPython(args.toString(), new BridgeCallbackWarp(callback) {
            @Override
            protected void onCallbackInternal(BridgeResult result) {
                if (result.code == BridgeResultCode.SUCCESS) {
                    unRegisterPhoneStateObserver();
                }
            }
        });
    }

    /**
     * 上传蓝牙手柄脚本
     * @param args
     * @param callback
     */
    @JavascriptInterface
    public void uploadJoystickPython(BridgeObject args, final OnCallback callback) {
        if (!BluetoothHelper.isConnected()) {
            callback.onCallback(BridgeResult.FAIL());
            return;
        }
        BluetoothCommunicator.getInstance().uploadJoystickPython(args.toString(), new BridgeCallbackWarp(callback) {
            @Override
            protected void onCallbackInternal(BridgeResult result) {
            }
        });
    }

    /**
     * 获取指定id的动作文件内容
     */
    @JavascriptInterface
    public BridgeResult getMotion(String id) {
        Motion[] motions = new Motion[1];
        HashMap<String, Motion[]> resultData = new HashMap<>();
        resultData.put("content", motions);
        if (Workspace.getInstance() != null && Workspace.getInstance().getProject() != null) {
            motions[0] = Workspace.getInstance().getProject().getMotion(id);
        }
        BridgeResult result = BridgeResult.SUCCESS();
        result.data = resultData;
        return result;
    }

    @JavascriptInterface
    public BridgeResult getUkitNetworkState() {
        BridgeResult result = BridgeResult.SUCCESS();
        result.data = BridgeBoolean.wrap(CommonBoardNetworkStateHolder.getInstance().isNetworkConnected());
        return result;
    }

    @JavascriptInterface
    public BridgeResult getUkitWiFiState() {
        BridgeResult result = BridgeResult.SUCCESS();
        result.data = BridgeBoolean.wrap(CommonBoardNetworkStateHolder.getInstance().isWiFiConnected());
        return result;
    }

    /**
     * 播放声音
     */
    @JavascriptInterface
    public void playAudio(BridgeObject args, OnCallback callback) {
        if((int)Settings.getTargetDevice() == UkitDeviceCompact.UKIT_SMART_DEVICE) {
            BluetoothCommunicator.getInstance().playAudio(args.toString(), callback);
        } else if((int)Settings.getTargetDevice() == UkitDeviceCompact.UKIT_LEGACY_DEVICE) {
            MediaAudioPlayer.getInstance().playAudio(args, callback);
        }
    }

    @JavascriptInterface
    public BridgeResult stopAudio() {
        if((int)Settings.getTargetDevice() == UkitDeviceCompact.UKIT_SMART_DEVICE) {
            BluetoothCommunicator.getInstance().stopAudio();
        } else if((int)Settings.getTargetDevice() == UkitDeviceCompact.UKIT_LEGACY_DEVICE) {
            MediaAudioPlayer.getInstance().stopAudio();
        }
        BridgeResult result = BridgeResult.SUCCESS();
        result.data = BridgeBoolean.TRUE();
        return result;
    }

    /**
     * 获取遥控器窗口列表
     */
    @JavascriptInterface
    public BridgeResult getRemoteBlockList(String args) {
        BridgeResult result = BridgeResult.SUCCESS();
        result.data = ControllerManager.findWidgetConfigByType(args);
        return result;
    }

    /**
     * 获取遥控器窗口列表
     */
    @JavascriptInterface
    public void servoDropPower(OnCallback callback) {
        BluetoothCommunicator.getInstance().servoDropPower(callback);
    }

    @JavascriptInterface
    public void openAudioList(String currentAudioId, APICallback<BridgeObject> callback) {
        if((int)Settings.getTargetDevice() == UkitDeviceCompact.UKIT_SMART_DEVICE) {
            AudioListHelper.openUKitAudioList(currentAudioId, callback);
        } else if((int)Settings.getTargetDevice() == UkitDeviceCompact.UKIT_LEGACY_DEVICE) {
            AudioListHelper.openLocalAudioList(currentAudioId, callback);
        } else {
            Map<Object, Object> result = new HashMap<>();
            result.put(BlocklyConstants.IS_SELECT, BridgeBoolean.wrap(false));
            callback.onCallback(new BridgeObject(result), true);
        }
    }

    @JavascriptInterface
    public List<BlocklyAudio> getAudioList() throws Exception {
        if (Workspace.getInstance().getProject() != null &&
                Workspace.getInstance().getProject().audioList != null) {
            return Workspace.getInstance().getProject().audioList;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public String getNamespace() {
        return NAME_SPACE;
    }

//    @Override
//    public void onConnectStateChanged(UKitRemoteDevice device, boolean oldState, boolean newState, boolean verified) {
//        if (!newState) {
//            if (mQueryAllSensorsTimer != null) {
//                mQueryAllSensorsTimer.stop();
//                mQueryAllSensorsTimer = null;
//            }
//        }
//        Object[] args = new Object[]{BridgeBoolean.wrap(newState)};
//        if (BridgeCommunicator.getInstance().getBlocklyBridge(false).isCommunicable()) {
//            BridgeCommunicator.getInstance().getBlocklyBridge(false).call(BlocklyFunctions.notifyBluetoothStateChange, args, null);
//        }
//        if (BridgeCommunicator.getInstance().getBlocklyBridge(true).isCommunicable()) {
//            BridgeCommunicator.getInstance().getBlocklyBridge(true).call(BlocklyFunctions.notifyBluetoothStateChange, args, null);
//        }
//    }

    @Override
    public void onConnectStatusChanged(URoProduct product, URoConnectStatus connectStatus) {
        if (connectStatus == URoConnectStatus.DISCONNECTED) {
            if (mQueryAllSensorsTimer != null) {
                mQueryAllSensorsTimer.stop();
                mQueryAllSensorsTimer = null;
            }
        }
        Object[] args = new Object[]{BridgeBoolean.wrap(connectStatus == URoConnectStatus.CONNECTED)};
        if (BridgeCommunicator.getInstance().getBlocklyBridge(false).isCommunicable()) {
            BridgeCommunicator.getInstance().getBlocklyBridge(false).call(BlocklyFunctions.notifyBluetoothStateChange, args, null);
        }
        if (BridgeCommunicator.getInstance().getBlocklyBridge(true).isCommunicable()) {
            BridgeCommunicator.getInstance().getBlocklyBridge(true).call(BlocklyFunctions.notifyBluetoothStateChange, args, null);
        }
    }

    private void callResult(OnCallback callback, BridgeResult bridgeResult) {
        if (callback != null) {
            callback.onCallback(bridgeResult);
        }
    }

    private abstract class BridgeCallbackWarp implements OnCallback {
        private OnCallback outter;

        public BridgeCallbackWarp(OnCallback outter) {
            this.outter = outter;
        }

        protected abstract void onCallbackInternal(BridgeResult result);

        @Override
        public void onCallback(BridgeResult result) {
            onCallbackInternal(result);
            if(outter != null) {
                outter.onCallback(result);
            }
        }

    }

}
