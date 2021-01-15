package com.ubtedu.ukit.bluetooth.processor;

import android.text.TextUtils;

import com.ubtedu.bridge.BridgeObject;
import com.ubtedu.bridge.BridgeResult;
import com.ubtedu.bridge.OnCallback;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoPushMessageReceivedListener;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentID;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoError;
import com.ubtedu.deviceconnect.libs.base.model.event.URoPushMessageData;
import com.ubtedu.deviceconnect.libs.base.model.event.URoPushMessageType;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoNetworkState;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoWiFiStatusInfo;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.BtInvocationFactory;
import com.ubtedu.ukit.bluetooth.utils.BtLogUtils;
import com.ubtedu.ukit.project.bridge.BluetoothCommunicator;
import com.ubtedu.ukit.project.bridge.BridgeCommunicator;
import com.ubtedu.ukit.project.bridge.MediaAudioPlayer;
import com.ubtedu.ukit.project.bridge.api.ToastHelper;
import com.ubtedu.ukit.project.bridge.functions.BlocklyFunctions;
import com.ubtedu.ukit.project.controller.ControllerModeHolder;
import com.ubtedu.ukit.project.controller.manager.ControllerManager;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @Author naOKi
 * @Date 2018/11/08
 **/
public class CommonPushMessageEventHandler implements URoPushMessageReceivedListener {

    private CommonPushMessageEventHandler() {
    }

    private static CommonPushMessageEventHandler instance = null;

    public static CommonPushMessageEventHandler getInstance() {
        synchronized (CommonPushMessageEventHandler.class) {
            if (instance == null) {
                instance = new CommonPushMessageEventHandler();
            }
            return instance;
        }
    }

    @Override
    public void onPushMessageReceived(URoProduct product, URoPushMessageType type, int subType, URoPushMessageData data) {
        try {
            if (URoPushMessageType.SCRIPT_REPORT.equals(type)) {
                if (PyScriptRunningStateHolder.getInstance().isScriptStopped()){
                    return;
                }
                URoScriptMessageType scriptMessageType = URoScriptMessageType.findType(subType);
                if (URoScriptMessageType.SCRIPT_HIGHLIGHT.equals(scriptMessageType)) {
                    if (BridgeCommunicator.getInstance().getBlocklyBridge(false).isCommunicable()) {
                        JSONObject jsonObject = new JSONObject((String) data.getValue());
                        Object[] args = new Object[]{jsonObject.getString("id")};
                        BridgeCommunicator.getInstance().getBlocklyBridge(false).call(BlocklyFunctions.setHighlight, args, null);
                    }
                } else if (URoScriptMessageType.SCRIPT_COMPONENT_FAULT.equals(scriptMessageType)) {
                    JSONObject jsonObject = new JSONObject((String) data.getValue());
                    String _type = jsonObject.getString("type");
                    JSONArray ids = jsonObject.getJSONArray("ids");
                    URoComponentType componentType = null;
                    switch (_type) {
                        case "infrared":
                            componentType = URoComponentType.INFRAREDSENSOR;
                            break;
                        case "touch":
                            componentType = URoComponentType.TOUCHSENSOR;
                            break;
                        case "ultrasound":
                            componentType = URoComponentType.ULTRASOUNDSENSOR;
                            break;
                        case "color":
                            componentType = URoComponentType.COLORSENSOR;
                            break;
                        case "humiture":
                            componentType = URoComponentType.ENVIRONMENTSENSOR;
                            break;
                        case "envLight":
                            componentType = URoComponentType.BRIGHTNESSSENSOR;
                            break;
                        case "sound":
                            componentType = URoComponentType.SOUNDSENSOR;
                            break;
                        case "servo":
                            componentType = URoComponentType.SERVOS;
                            break;
                        case "motor":
                            componentType = URoComponentType.MOTOR;
                            break;
                        case "led":
                            componentType = URoComponentType.LED;
                            break;
                        case "lightBox":
                            componentType = URoComponentType.LED_BELT;
                            break;
                    }
                    if(componentType != null && ids != null && ids.length() > 0) {
                        for(int i = 0; i < ids.length(); i++) {
                            int id = ids.getInt(i);
                            URoComponentID componentID = new URoComponentID(componentType, id);
                            PeripheralErrorCollector.getInstance().appendUsedPeripheral(componentType, id);
                            PeripheralErrorCollector.getInstance().onReportComponentError(null, componentID, URoError.UNKNOWN);
                        }
                        BtLogUtils.d("上报脚本执行错误: type=%s, ids=%s", _type, ids.toString());
                    }
                } else if (URoScriptMessageType.SCRIPT_STOP_RUNNING.equals(scriptMessageType)) {
                    String jsonString = (String) data.getValue();
//                    BtLogUtils.d("脚本停止:%s", jsonString);
                    if (!TextUtils.isEmpty(jsonString)) {
                        if (PyScriptRunningStateHolder.getInstance().isControllerScriptRunning()
                                && jsonString.contains("ubt_controller.py")) {//遥控器脚本停止
                            PyScriptRunningStateHolder.getInstance().setScriptStopped();//更新脚本状态为停止，防止ControllerManager中调用stopPython停掉运行中的离线脚本，必须在切换ControllerMode前调用
                            ControllerModeHolder.setControllerMode(ControllerModeHolder.MODE_EDIT);
                            ToastHelper.toastShort(UKitApplication.getInstance().getString(R.string.script_running_interrupt_msg));
                        } else if (PyScriptRunningStateHolder.getInstance().isBlocklyScriptRunning()
                                && jsonString.contains("ubt_blockly.py")) {//blockly在线脚本停止
                            PyScriptRunningStateHolder.getInstance().setScriptStopped();
                            BluetoothCommunicator.getInstance().cancelSendPython();
                            if (BridgeCommunicator.getInstance().getBlocklyBridge(false).isCommunicable()) {
                                BridgeCommunicator.getInstance().getBlocklyBridge(false).call(BlocklyFunctions.terminateExecProgram, new Object[]{true}, null);//{true}表示Blockly调用stopPython接口时不发送停脚本指令
                            }
                            PeripheralErrorCollector.getInstance().updateCollectorState(PeripheralErrorCollector.CollectorState.STOP, null);
                            ToastHelper.toastShort(UKitApplication.getInstance().getString(R.string.script_running_interrupt_msg));
                        }
                    }
                } else if (URoScriptMessageType.SCRIPT_START_RUNNING.equals(scriptMessageType)) {
                    PyScriptRunningStateHolder.getInstance().setScriptExecuting();
                } else if (URoScriptMessageType.SCRIPT_START_PLAY_AUDIO.equals(scriptMessageType)) {
                    BridgeObject jsonObject = new BridgeObject((String) data.getValue());
                    String callbackVar = jsonObject.getString("callbackVar");
                    jsonObject.remove("callbackVar");
                    jsonObject.remove("isdelay");
                    jsonObject.put("isdelay", 1);
                    MediaAudioPlayer.getInstance().playAudio(jsonObject, new OnCallback() {
                        @Override
                        public void onCallback(BridgeResult result) {
                            BluetoothHelper.addCommand(BtInvocationFactory.updateScriptValue(callbackVar, "1", null));
                        }
                    });
                } else if (URoScriptMessageType.SCRIPT_STOP_PLAY_AUDIO.equals(scriptMessageType)) {
                    MediaAudioPlayer.getInstance().stopAudio();
                } else if (URoScriptMessageType.SCRIPT_PLAY_AUDIO_FAULT.equals(scriptMessageType)) {
                    JSONObject jsonObject = new JSONObject((String) data.getValue());
                    BtLogUtils.d("录音文件播放是失败: [%s]", jsonObject.getString("name"));
                    PeripheralErrorCollector.getInstance().onReportRecordingError(jsonObject.getString("name"));
                } else if (URoScriptMessageType.SCRIPT_UPDATE_READER_VALUE_VIEW.equals(scriptMessageType)) {
                    if (ControllerModeHolder.getControllerMode() != ControllerModeHolder.MODE_EXECUTION || ControllerManager.isScriptRestarting()) {
                        return;
                    }
                    JSONObject jsonObject = new JSONObject((String) data.getValue());
                    String id = jsonObject.getString("id");
                    int value = jsonObject.getInt("value");
                    ControllerManager.updateReaderValueView(id, Math.min(Math.max(value, -1000), 1000));
                } else if (URoScriptMessageType.SCRIPT_UPDATE_READER_COLOR_VIEW.equals(scriptMessageType)) {
                    if (ControllerModeHolder.getControllerMode() != ControllerModeHolder.MODE_EXECUTION || ControllerManager.isScriptRestarting()) {
                        return;
                    }
                    JSONObject jsonObject = new JSONObject((String) data.getValue());
                    String id = jsonObject.getString("id");
                    JSONArray value = jsonObject.getJSONArray("value");
                    if(value != null && value.length() == 3) {
                        ControllerManager.updateReaderColorView(id, (value.getInt(0) << 16 | value.getInt(1) << 8 | value.getInt(2)) & 0xFFFFFF);
                    }
                } else if (URoScriptMessageType.SCRIPT_CONTROLLER_RUNNING_STATUS.equals(scriptMessageType)) {
                    if (ControllerModeHolder.getControllerMode() != ControllerModeHolder.MODE_EXECUTION || ControllerManager.isScriptRestarting()) {
                        return;
                    }
                    JSONObject jsonObject = new JSONObject((String) data.getValue());
                    BtLogUtils.d("脚本运行发生变化: %s %s", jsonObject.getString("subscribeId"), jsonObject.getString("status"));
                    String subscribeId = jsonObject.getString("subscribeId");
                    if (!TextUtils.isEmpty(subscribeId)) {
                        if ("start".equals(jsonObject.getString("status"))) {
                            ControllerManager.addScripExecEvent(subscribeId);
                        } else if ("end".equals(jsonObject.getString("status"))) {
                            ControllerManager.removeScripExecEvent(subscribeId);
                        }
                    }
                } else if (URoScriptMessageType.SCRIPT_RUNNING_ERROR.equals(scriptMessageType)) {
                    JSONObject jsonObject = new JSONObject((String) data.getValue());
                    BtLogUtils.d("脚本运行发生错误: %s msg:%s", jsonObject.getString("exception"), jsonObject.getString("msg"));
                    String errorType = jsonObject.getString("exception");
                    String msg = jsonObject.getString("msg");
                    URoScriptErrorType scriptErrorType = URoScriptErrorType.findErrorType(errorType, msg);
                    if (scriptErrorType == null) {
                        msg = UKitApplication.getInstance().getString(R.string.script_running_error_msg);
                    } else {
                        switch (scriptErrorType) {
                            case SCRIPT_ZERO_DIVISION_ERROR:
                                msg = UKitApplication.getInstance().getString(R.string.project_controller_divisor_zero_msg);
                                break;
                            case SCRIPT_MAXIMUM_RECURSION_DEPTH_ERROR:
                                msg = UKitApplication.getInstance().getString(R.string.project_controller_stack_overflow_msg);
                                break;
                            default:
                                msg = UKitApplication.getInstance().getString(R.string.script_running_error_msg);
                                break;
                        }
                    }
                    ToastHelper.toastShort(msg);
                    BluetoothHelper.terminateExecution();
                    if (ControllerModeHolder.getControllerMode() == ControllerModeHolder.MODE_EXECUTION) {
                        ControllerModeHolder.setControllerMode(ControllerModeHolder.MODE_EDIT);
                    }
                }
            } else if (URoPushMessageType.WIFI_STATUS_CHANGE.equals(type)) {
                URoWiFiStatusInfo info = (URoWiFiStatusInfo) data.getValue();
                URoLogUtils.d(info.toString());
                CommonBoardNetworkStateHolder.getInstance().updateWifiState(info);
            } else if (URoPushMessageType.NETWORK_STATUS_CHANGE.equals(type)) {
                URoNetworkState state = (URoNetworkState) data.getValue();
                URoLogUtils.d(state.toString());
                CommonBoardNetworkStateHolder.getInstance().updateNetworkState(state);
            } else if (URoPushMessageType.SCRIPT_RUNNING_FAULT.equals(type)) {
                //todo 主控未支持
                BluetoothHelper.terminateExecution();
                BtLogUtils.e("上报脚本执行异常");
            }
        } catch (Throwable e) {
            //do nothing
        }
    }

    public enum URoScriptMessageType {
        SCRIPT_HIGHLIGHT(0), // 高亮
        SCRIPT_COMPONENT_FAULT(1), // 外设异常
        SCRIPT_STOP_RUNNING(2), // 停止
        SCRIPT_START_PLAY_AUDIO(3), // 播放音效
        SCRIPT_STOP_PLAY_AUDIO(4), // 停止播放音效
        SCRIPT_PLAY_AUDIO_FAULT(5), // 播放主控录音失败
        SCRIPT_UPDATE_READER_VALUE_VIEW(6), // 读数窗口显示数值
        SCRIPT_UPDATE_READER_COLOR_VIEW(7), // 颜色窗口显示颜色
        SCRIPT_CONTROLLER_RUNNING_STATUS(8),//遥控器组件对应脚本执行状态变化start/end
        SCRIPT_START_RUNNING(9),//脚本开始运行
        SCRIPT_RUNNING_ERROR(255)//脚本运行异常
        ;

        private int type;

        public int getType() {
            return type;
        }

        URoScriptMessageType(int type) {
            this.type = type;
        }

        public static URoScriptMessageType findType(int type) {
            URoScriptMessageType result = null;
            for (URoScriptMessageType item : URoScriptMessageType.values()) {
                if (item.type == type) {
                    result = item;
                    break;
                }
            }
            return result;
        }

    }

    public enum URoScriptErrorType {
        SCRIPT_MAXIMUM_RECURSION_DEPTH_ERROR("RuntimeError", "maximum recursion depth"), //栈溢出
        SCRIPT_ZERO_DIVISION_ERROR("ZeroDivisionError", "divide by zero")//除数为0
        ;

        private String errorType;
        private String errorMsg;

        URoScriptErrorType(String errorType, String errorMsg) {
            this.errorType = errorType;
            this.errorMsg = errorMsg;
        }

        public static URoScriptErrorType findErrorType(String errorType, String errorMsg) {
            if (TextUtils.isEmpty(errorType)) {
                return null;
            }
            if (SCRIPT_ZERO_DIVISION_ERROR.errorType.equals(errorType)) {
                return SCRIPT_ZERO_DIVISION_ERROR;
            }
            if (TextUtils.isEmpty(errorMsg)) {
                return null;
            }
            URoScriptErrorType result = null;
            for (URoScriptErrorType item : URoScriptErrorType.values()) {
                if (item.errorType.equals(errorType) && errorMsg.toLowerCase().contains(item.errorMsg)) {
                    result = item;
                    break;
                }
            }
            return result;
        }
    }
}
