package com.ubtedu.ukit.project.controller.manager;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import com.ubtedu.alpha1x.core.base.fragment.OnDialogFragmentDismissListener;
import com.ubtedu.alpha1x.utils.MD5Util;
import com.ubtedu.bridge.BridgeResult;
import com.ubtedu.bridge.BridgeResultCode;
import com.ubtedu.bridge.OnCallback;
import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoMissionCallback;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocationSequence;
import com.ubtedu.deviceconnect.libs.base.invocation.URoMissionAbortSignal;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoFileStat;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.BtInvocationFactory;
import com.ubtedu.ukit.bluetooth.ErrorCollectSequenceCallback;
import com.ubtedu.ukit.bluetooth.IUKitCommandResponse;
import com.ubtedu.ukit.bluetooth.UkitDeviceCompact;
import com.ubtedu.ukit.bluetooth.UkitInvocation;
import com.ubtedu.ukit.bluetooth.dialog.SendScriptFileDialogFragment;
import com.ubtedu.ukit.bluetooth.error.BluetoothCommonErrorHelper;
import com.ubtedu.ukit.bluetooth.processor.PeripheralErrorCollector;
import com.ubtedu.ukit.bluetooth.processor.PyScriptRunningStateHolder;
import com.ubtedu.ukit.common.base.UKitBaseActivity;
import com.ubtedu.ukit.common.utils.MicroPythonUtils;
import com.ubtedu.ukit.common.utils.UuidUtil;
import com.ubtedu.ukit.menu.settings.Settings;
import com.ubtedu.ukit.project.Workspace;
import com.ubtedu.ukit.project.bridge.BluetoothCommunicator;
import com.ubtedu.ukit.project.bridge.DeviceDirectionHelper;
import com.ubtedu.ukit.project.bridge.MediaAudioPlayer;
import com.ubtedu.ukit.project.bridge.QueryAllSensorsTimer;
import com.ubtedu.ukit.project.bridge.api.ActivityHelper;
import com.ubtedu.ukit.project.bridge.api.ToastHelper;
import com.ubtedu.ukit.project.bridge.arguments.PhoneStateArguments;
import com.ubtedu.ukit.project.controller.ControllerModeHolder;
import com.ubtedu.ukit.project.controller.interfaces.IControllerModeChangeListener;
import com.ubtedu.ukit.project.controller.lua.LuaExecutor;
import com.ubtedu.ukit.project.controller.model.config.ButtonCustomConfig;
import com.ubtedu.ukit.project.controller.model.config.ButtonCustomConfigV2;
import com.ubtedu.ukit.project.controller.model.config.Joystick4DirectionConfig;
import com.ubtedu.ukit.project.controller.model.config.SliderValueConfig;
import com.ubtedu.ukit.project.controller.model.config.SwitchToggleConfig;
import com.ubtedu.ukit.project.controller.model.config.SwitchTouchConfig;
import com.ubtedu.ukit.project.controller.model.config.WidgetConfig;
import com.ubtedu.ukit.project.controller.model.define.WidgetType;
import com.ubtedu.ukit.project.controller.utils.ControllerConstValue;
import com.ubtedu.ukit.project.controller.widget.ButtonCustomView;
import com.ubtedu.ukit.project.controller.widget.ButtonCustomViewV2;
import com.ubtedu.ukit.project.controller.widget.CellContainer;
import com.ubtedu.ukit.project.controller.widget.ControllerWidgetView;
import com.ubtedu.ukit.project.controller.widget.ReaderColorView;
import com.ubtedu.ukit.project.controller.widget.ReaderValueView;
import com.ubtedu.ukit.project.vo.BlocklyFile;
import com.ubtedu.ukit.project.vo.Controller;
import com.ubtedu.ukit.project.vo.ControllerBlockly;
import com.ubtedu.ukit.project.vo.Motion;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author naOKi
 * @Date 2018/12/25
 **/
public class ControllerManager {

    private ControllerManager() {
    }

    private static Controller mController;
    private static boolean isModified = false;
    private static final Map<String, Object> cacheData = new HashMap<>();
    private static final Map<String, ExecutionData> cacheExecution = new HashMap<>();
    private static final Map<String, Boolean> cacheJoystick = new HashMap<>();
    private static final HashSet<IExecutionStatusChangeListener> listeners = new HashSet<>();
    private static WeakReference<CellContainer> containerWeakReference = null;

    private static boolean executionFlag = false;
    private static boolean backgroundFlag = false;

    private static QueryAllSensorsTimer sensorTimer = null;
    private static DeviceDirectionHelper directionHelper = null;
    private static String direction = null;

    private static URoMissionAbortSignal sendAbortSignal = null;

    private static SendScriptFileDialogFragment mSendScriptDialogFragment = null;

    private static Handler handler = new Handler(Looper.getMainLooper());

    private static final HashMap<String, Runnable> sliderRunnableMap = new HashMap<>();

    private static final String SCRIPT_VALUE_ON = "on";
    private static final String SCRIPT_VALUE_OFF = "off";
    private static final String SCRIPT_VALUE_DOWN = "down";
    private static final String SCRIPT_VALUE_UP = "up";
    private static final String SCRIPT_EVENT_ON = "e_on";
    private static final String SCRIPT_EVENT_OFF = "e_off";
    private static final String SCRIPT_EVENT_DOWN = "e_down";
    private static final String SCRIPT_EVENT_UP = "e_up";
    private static final String SCRIPT_EVENT_CHANGE = "e_change";

    private static boolean isScriptRestarting =false;

    public static boolean isScriptRestarting() {
        synchronized (ControllerManager.class) {
            return isScriptRestarting && isSmartDevice();
        }
    }

    public static void setScriptRestarting(boolean restart) {
        synchronized (ControllerManager.class) {
            isScriptRestarting = restart;
        }
    }

    private static IControllerModeChangeListener listener = new IControllerModeChangeListener() {
        @Override
        public void onControllerModeChanged(int controllerMode) {
            if (controllerMode == ControllerModeHolder.MODE_EDIT) {
                stopSensorsTimer();
                stopDeviceDirection();
                terminateExecution();
            } else if (controllerMode == ControllerModeHolder.MODE_EXECUTION) {
                PeripheralErrorCollector.getInstance().updateCollectorState(PeripheralErrorCollector.CollectorState.START, PeripheralErrorCollector.ErrorCollectorType.CONTROLLER);
                startSensorsTimer();
                if(!isSmartDevice()) {
                    startDeviceDirection(); // 新主控需要发送完脚本后再启动
                }
            }
        }
    };

    public static void enterExecutionMode(){
        if (ControllerModeHolder.MODE_EXECUTION == ControllerModeHolder.getControllerMode()) {
            return;
        }
        if (isSmartDevice()){
            //启动脚本成功后再更新为MODE_EXECUTION
            startPythonScript(true, true);
        }else{
            ControllerModeHolder.setControllerMode(ControllerModeHolder.MODE_EXECUTION);
        }
    }

    static {
        ControllerModeHolder.addControllerModeChangeListener(listener);
    }

    private static class ExecutionData {
        private URoMissionAbortSignal bas = null;
        private LuaExecutor le = null;
        private long timestamp = -1;

        private boolean terminate() {
            return terminate(null, null);
        }

        private boolean terminate(LuaExecutor le) {
            return terminate(null, le);
        }

        private boolean terminate(URoMissionAbortSignal bas) {
            return terminate(bas, null);
        }

        private boolean terminate(URoMissionAbortSignal _bas, LuaExecutor _le) {
            boolean basTerminate = false;
            boolean leTerminate = false;
            if (bas != null) {
                if (_bas == null || _bas.equals(bas)) {
                    bas.abort();
                    bas = null;
                    basTerminate = true;
                }
            }
            if (le != null) {
                if (_le == null || _le.equals(le)) {
                    le.terminate();
                    le = null;
                    leTerminate = true;
                }
            }
            return leTerminate || basTerminate;
        }
    }

    public interface IExecutionStatusChangeListener {
        void onExecutionStatusChanged(boolean hasExecution);
        void onPauseExecutionStart();
        void onPauseExecutionEnd();
    }

    public static boolean isSmartDevice() {
        return (int)Settings.getTargetDevice() == UkitDeviceCompact.UKIT_SMART_DEVICE;
//        return false;
    }

    private static void startPythonScript(boolean merge, boolean send) {
        startPythonScript(merge, send, false, null);
    }

    private static void startPythonScript(boolean merge, boolean send, boolean startAgain, final HashSet<String> skipCheckBlocklyId) {
        if(!isSmartDevice() || !BluetoothHelper.isConnected()) {
            return;
        }
//        File file = new File(UKitApplication.getInstance().getCacheDir(), "ubt_controller.py");
        String fileName = "/ubt_controller.py";
        String mergedContent = null;
        boolean needSend = false;
        boolean toFullProgress=false;
        if(merge) {
            if(!MicroPythonUtils.hasCommonPart()) {
                if(startAgain) {
                    //防止死循环
                    return;
                }
                MicroPythonUtils.loadCommonPart(new Runnable() {
                    @Override
                    public void run() {
                        startPythonScript(merge, send, true, skipCheckBlocklyId);
                    }
                });
                return;
            }
            boolean hasScript = false;
            HashSet<String> importList = new HashSet<>();
            StringBuilder sb = new StringBuilder();
            synchronized (ControllerManager.class) {
                for (WidgetConfig widgetConfig : mController.configs) {
                    if (widgetConfig == null) {
                        continue;
                    }
                    if (widgetConfig instanceof ButtonCustomConfig && ((ButtonCustomConfig) widgetConfig).getMode() != WidgetConfig.MODE_EXECUTION_BLOCKLY) {
                        continue;
                    }
                    if (widgetConfig instanceof SwitchToggleConfig && ((SwitchToggleConfig) widgetConfig).getMode() != WidgetConfig.MODE_EXECUTION_BLOCKLY) {
                        continue;
                    }
                    if (widgetConfig instanceof SwitchTouchConfig && ((SwitchTouchConfig) widgetConfig).getMode() != WidgetConfig.MODE_EXECUTION_BLOCKLY) {
                        continue;
                    }
                    if (TextUtils.isEmpty(widgetConfig.blocklyId)) {
                        continue;
                    }
                    ControllerBlockly controllerBlockly = findControllerBlocklyById(widgetConfig.blocklyId);
                    String microPythonContent = findControllerFileContent(widgetConfig.blocklyId, BlocklyFile.NAME_MICRO_PYTHON);
                    if (controllerBlockly != null && !TextUtils.isEmpty(controllerBlockly.workspace) && TextUtils.isEmpty(microPythonContent)) {
                        if(skipCheckBlocklyId != null && skipCheckBlocklyId.contains(widgetConfig.blocklyId)) {
                            continue;
                        }
                        MicroPythonUtils.xmlToPython(widgetConfig.subscribeId,controllerBlockly.workspace, new MicroPythonUtils.Xml2PythonCallback() {
                            @Override
                            public void onCallback(boolean isSuccess, String xml, String content) {
                                HashSet<String> skipIds = new HashSet<>();
                                if(skipCheckBlocklyId != null) {
                                    skipIds.addAll(skipCheckBlocklyId);
                                }
                                skipIds.add(widgetConfig.blocklyId);
                                if(isSuccess) {
                                    setBlocklyFileContent(widgetConfig.blocklyId, BlocklyFile.NAME_MICRO_PYTHON, content);
                                }
                                startPythonScript(merge, send, startAgain, skipIds);
                            }
                        });
                        return;
                    }
                    String microPythonPart = MicroPythonUtils.conversionPython(widgetConfig.getId(), importList, microPythonContent, null);
                    sb.append(microPythonPart);
                    needSend = true;
                    hasScript = true;
                }
            }
            if(!hasScript) {
                //没有需要生成的脚本，不需要上传脚本
                toFullProgress=true;
            }
            mergedContent = MicroPythonUtils.mergedMicroPythonContent(importList, sb.toString());
        }
        if(needSend || send) {
            if(TextUtils.isEmpty(mergedContent)) {
                //没有生成脚本
                toFullProgress=true;
            }
        }
        SendScriptFileDialogFragment.SendScriptDialogUI ui = null;
        UKitBaseActivity activity = ActivityHelper.getResumeActivity();
        if (activity != null) {
            if (mSendScriptDialogFragment != null && mSendScriptDialogFragment.isVisible()) {
                return;
            }
            SendScriptFileDialogFragment dialogFragment = SendScriptFileDialogFragment.newBuilder()
                    .autoClose(true)
                    .processingText(activity.getString(R.string.remote_control_online_sending_script_file_title), activity.getString(R.string.remote_control_online_sending_script_file_message))
                    .failureText(activity.getString(R.string.remote_control_online_sending_script_file_failure_title), "", activity.getString(R.string.blockly_online_sending_script_file_ok))
                    .build();
            ui = dialogFragment.getUI();
            dialogFragment.setDismissListener(new OnDialogFragmentDismissListener() {
                @Override
                public void onDismiss(Object... value) {
                    if (value != null && value.length > 0 && SendScriptFileDialogFragment.Status.SUCCESS == value[0]) {
                        ControllerModeHolder.setControllerMode(ControllerModeHolder.MODE_EXECUTION);
                    }
                    mSendScriptDialogFragment = null;
                }
            });
            dialogFragment.setConfirmBtnClickListener(new SendScriptFileDialogFragment.OnResultConfirmBtnClickListener() {
                @Override
                public void onConfirmBtnClick(boolean isSuccess) {
                    if (!isSuccess){
                        ControllerModeHolder.setControllerMode(ControllerModeHolder.MODE_EDIT);
                        dialogFragment.dismiss();
                    }
                }
            });
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (mSendScriptDialogFragment != null && mSendScriptDialogFragment.isVisible()) {
                        return;
                    }
                    dialogFragment.show(activity.getSupportFragmentManager(), "send_script");
                    mSendScriptDialogFragment = dialogFragment;
                }
            });
        }

        SendScriptFileDialogFragment.SendScriptDialogUI _ui = ui;
        if (toFullProgress){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (_ui != null) {
                        _ui.updateUiPercent(100);
                        _ui.updateUiStatus(SendScriptFileDialogFragment.Status.SUCCESS);
                    }
                }
            });
            return;
        }

        String _mergedContent = mergedContent;
        boolean _needSend = needSend;
        URoInvocationSequence sequence = new URoInvocationSequence();
        sequence.setCompletionCallback(new URoMissionCallback() {
            @Override
            public void onComplete(URoCompletionResult result) {
                if(result.isSuccess()) {
                    startDeviceDirection();
                    postWidgetDefaultValue();
                }
                if (_ui != null) {
                    _ui.updateUiStatus(result.isSuccess() ? SendScriptFileDialogFragment.Status.SUCCESS : SendScriptFileDialogFragment.Status.FAILURE);
                }
            }

            @Override
            public void onMissionNextStep(int currentStep, int totalStep) {

            }

            @Override
            public void onMissionBegin() {
                sendAbortSignal = getAbortSignal();
            }

            @Override
            public void onMissionEnd() {
                if (getAbortSignal() != null && getAbortSignal().isAbort()) {
                    if (_ui != null) {
                        _ui.updateUiStatus(SendScriptFileDialogFragment.Status.CLOSE);
                    }
                }
                sendAbortSignal = null;
            }
        });
        BluetoothHelper.addCommand(BtInvocationFactory.stopExecScript(null));
        UkitInvocation invocation = BtInvocationFactory.getFileStat(fileName, new IUKitCommandResponse<URoFileStat>() {
            @Override
            protected void onUKitCommandResponse(URoCompletionResult<URoFileStat> result) {
                boolean needSendFile = true;
                byte[] fileContent = _mergedContent.getBytes();
                if (result.isSuccess() && !TextUtils.isEmpty(result.getData().getMd5())) {
                    String fileMd5 = MD5Util.encodeMd5(fileContent);
                    if (TextUtils.equals(fileMd5.toLowerCase(), result.getData().getMd5().toLowerCase())) {
                        needSendFile = false;
                    }
                }
                sequence.action(BtInvocationFactory.stopExecScript(null));
                if (needSendFile) {
                    sequence.action(BtInvocationFactory.sendFileData(fileContent, fileName, new URoMissionCallback() {
                        @Override
                        public void onMissionNextStep(int currentStep, int totalStep) {
                        }

                        @Override
                        public void onProcessPercentChanged(int percent) {
                            if (_ui != null) {
//                                  BtLogUtils.d("Percent: %d", percent);
                                _ui.updateUiPercent(percent);
                            }
                        }

                        @Override
                        public void onMissionBegin() {
                        }

                        @Override
                        public void onMissionEnd() {
                        }

                        @Override
                        public void onComplete(URoCompletionResult result) {
                        }
                    }));
                    sequence.sleep(500);
                } else {
                    if (_ui != null) {
                        _ui.updateUiPercent(100);
                    }
                }
                sequence.action(BtInvocationFactory.stopExecScript(null));
                sequence.action(BtInvocationFactory.startExecScript(fileName, new IUKitCommandResponse<Void>() {
                    @Override
                    protected void onUKitCommandResponse(URoCompletionResult<Void> result) {
                        if (result.isSuccess()){
                            PyScriptRunningStateHolder.getInstance().setControllerScriptStarted();
                        }
                    }
                }));
                BluetoothHelper.addCommand(sequence);
            }
        });
        invocation.setTimeoutThreshold(1000);
        BluetoothHelper.addCommand(invocation);
    }

    private static void stopPythonScript() {
        if(!isSmartDevice()) {
            return;
        }
        if(sendAbortSignal != null && !sendAbortSignal.isAbort()) {
            sendAbortSignal.abort();
            sendAbortSignal = null;
        }
        URoInvocationSequence sequence = new URoInvocationSequence();
        sequence.action(BtInvocationFactory.stopExecScript(null));
        sequence.action(BtInvocationFactory.stopRunning(null));
        BluetoothHelper.addCommand(sequence);
    }

    public static void pause() {
        setBackgroundFlag(true);
        if (ControllerModeHolder.getControllerMode() == ControllerModeHolder.MODE_EXECUTION) {
            stopSensorsTimer();
            stopDeviceDirection();
            terminateExecution();
            PeripheralErrorCollector.getInstance().updateCollectorState(PeripheralErrorCollector.CollectorState.STOP, null);
        }
    }

    public static void resume() {
        setBackgroundFlag(false);
        if (ControllerModeHolder.getControllerMode() == ControllerModeHolder.MODE_EXECUTION) {
            if (BluetoothHelper.isConnected()) {
                if (Settings.isChargingProtection() && BluetoothHelper.isCharging()) {
                    BluetoothHelper.terminateExecution();
                    ControllerModeHolder.setControllerMode(ControllerModeHolder.MODE_EDIT);
                    //显示充电保护信息
                    BluetoothCommonErrorHelper.openBluetoothCommonErrorActivity(BluetoothCommonErrorHelper.CommonError.CHARGING_PROTECTION);
                    return;
                }
            }
            PeripheralErrorCollector.getInstance().updateCollectorState(PeripheralErrorCollector.CollectorState.START, PeripheralErrorCollector.ErrorCollectorType.CONTROLLER);
            startSensorsTimer();
            if(isSmartDevice()) {
                startPythonScript(true, true);
            } else {
                startDeviceDirection();
            }
        }
    }

    public static HashMap<String, String> getWidgetDefaultValue() {
        synchronized (ControllerManager.class) {
            if (mController == null) {
                return null;
            }
            HashMap<String, String> values = new HashMap<>();
            synchronized (cacheData) {
                for (WidgetConfig widgetConfig : mController.configs) {
                    if (!TextUtils.isEmpty(widgetConfig.subscribeId)) {
                        if (widgetConfig instanceof SliderValueConfig) {
                            SliderValueConfig sliderValueConfig = (SliderValueConfig) widgetConfig;
                            values.put(widgetConfig.subscribeId, String.valueOf(cacheData.containsKey(widgetConfig.id) ? getSliderValue(widgetConfig.id) : sliderValueConfig.getDefaultValue()));
                        } else if (widgetConfig instanceof SwitchToggleConfig || widgetConfig instanceof SwitchTouchConfig) {
                            values.put(widgetConfig.subscribeId, getSwitchStatus(widgetConfig.id) ? SCRIPT_VALUE_ON : SCRIPT_VALUE_OFF);
                        } else if (widgetConfig instanceof ButtonCustomConfig) {
                            values.put(widgetConfig.subscribeId, getButtonStatus(widgetConfig.id) ? SCRIPT_VALUE_DOWN : SCRIPT_VALUE_UP);
                        }
                    }
                }
            }
            return values;
        }
    }

    private static Runnable widgetDefaultValueRunnable = new Runnable() {
        @Override
        public void run() {
            HashMap<String, String> defaultValue = getWidgetDefaultValue();
            if (defaultValue != null && !defaultValue.isEmpty()) {
                BluetoothHelper.addCommand(BtInvocationFactory.updateScriptValues(defaultValue, null));
            }
        }
    };

    private static void postWidgetDefaultValue() {
        handler.postDelayed(widgetDefaultValueRunnable, 1500);
    }

    public static String getDeviceDirection() {
        return direction;
    }

    private static void startDeviceDirection() {
        stopDeviceDirection();
        directionHelper = new DeviceDirectionHelper();
        directionHelper.init(ActivityHelper.getResumeActivity());
        directionHelper.startDetect(new OnCallback() {
            private String phoneState = null;
            @Override
            public void onCallback(BridgeResult result) {
                if (result.code == BridgeResultCode.SUCCESS) {
                    PhoneStateArguments arguments = (PhoneStateArguments)result.data;
                    direction = arguments.state;
                    if(isSmartDevice()) {
                        if (arguments.state != null && (phoneState == null || !TextUtils.equals(phoneState, arguments.state))) {
                            phoneState = arguments.state;
                            BluetoothHelper.addCommand(BtInvocationFactory.updateScriptValue("ps", phoneState, null));
                        }
                    }
                }
            }
        });
    }

    private static void stopDeviceDirection() {
        if (directionHelper != null) {
            directionHelper.stopDetect();
            directionHelper = null;
        }
    }

    private static void startSensorsTimer() {
        if(isSmartDevice()) {
            return;
        }
        stopSensorsTimer();
        sensorTimer = QueryAllSensorsTimer.getInstance();
        sensorTimer.stop();
        sensorTimer.setCallback(null);
        sensorTimer.start();
    }

    private static void stopSensorsTimer() {
        if(isSmartDevice()) {
            return;
        }
        if (sensorTimer != null) {
            sensorTimer.stop();
            sensorTimer = null;
        }
    }

    public static void cleanup() {
        synchronized (cacheData) {
            mController = new Controller();
            cacheData.clear();
            cacheJoystick.clear();
            for(Runnable runnable : sliderRunnableMap.values()) {
                handler.removeCallbacks(runnable);
            }
            sliderRunnableMap.clear();
            terminateExecution();
        }
    }

    public static void setController(Controller controller) {
        synchronized (cacheData) {
            if (controller == null) {
                controller = new Controller();
            } else {
                //检查Controller，如果列表为空，则创建列表
                if (controller.configs == null) {
                    controller.configs = new ArrayList<>();
                }
                if (controller.blocklies == null) {
                    controller.blocklies = new ArrayList<>();
                } else {
                    for (ControllerBlockly blockly : controller.blocklies) {
                        if (blockly.blocklyFiles == null) {
                            blockly.blocklyFiles = new CopyOnWriteArrayList<>();
                        }
                    }
                }
//                if(Workspace.getInstance().getProject().projectVersion < Project.PROJECT_VERSION_WITH_MICRO_PYTHON) {
//                    for (WidgetConfig widgetConfig : controller.configs) {
//                        if (widgetConfig == null) {
//                            continue;
//                        }
//                        if (widgetConfig instanceof ButtonCustomConfig && ((ButtonCustomConfig) widgetConfig).getMode() != WidgetConfig.MODE_EXECUTION_BLOCKLY) {
//                            continue;
//                        }
//                        if (widgetConfig instanceof SwitchToggleConfig && ((SwitchToggleConfig) widgetConfig).getMode() != WidgetConfig.MODE_EXECUTION_BLOCKLY) {
//                            continue;
//                        }
//                        if (widgetConfig instanceof SwitchTouchConfig && ((SwitchTouchConfig) widgetConfig).getMode() != WidgetConfig.MODE_EXECUTION_BLOCKLY) {
//                            continue;
//                        }
//                        if (TextUtils.isEmpty(widgetConfig.blocklyId)) {
//                            continue;
//                        }
//                        ControllerBlockly controllerBlockly = findControllerBlocklyById(widgetConfig.blocklyId);
//                        String microPythonContent = findControllerFileContent(widgetConfig.blocklyId, BlocklyFile.NAME_MICRO_PYTHON);
//                        if (controllerBlockly != null && !TextUtils.isEmpty(controllerBlockly.workspace) && TextUtils.isEmpty(microPythonContent)) {
//                            MicroPythonUtils.xmlToPython(controllerBlockly.workspace, new MicroPythonUtils.Xml2PythonCallback() {
//                                @Override
//                                public void onCallback(boolean isSuccess, String xml, String content) {
//                                    if(isSuccess) {
//                                        setBlocklyFileContent(widgetConfig.blocklyId, BlocklyFile.NAME_MICRO_PYTHON, content);
//                                    }
//                                }
//                            });
//                        }
//                    }
//                }
            }
            ArrayList<WidgetConfig> tmpWidgetConfig = new ArrayList<>();
            if (controller.configs == null) {
                controller.configs = new ArrayList<>();
            }
            tmpWidgetConfig.addAll(controller.configs);
            controller.configs.clear();
            for (WidgetConfig widgetConfig : tmpWidgetConfig) {
                WidgetConfig wc = WidgetConfig.conversionWidgetConfig(widgetConfig);
                if (wc != null) {
                    controller.configs.add(wc);
                }
            }
            isModified = false;
            mController = controller;
            clearControllerPyFileContent();
            cacheData.clear();
            for (WidgetConfig widgetConfig : controller.configs) {
                if (!TextUtils.isEmpty(widgetConfig.subscribeId)) {
                    continue;
                }
                widgetConfig.subscribeId = findAvailableSubscribeId();
            }
            terminateExecution();
        }
    }

    public static void clearControllerPyFileContent() {
        synchronized (ControllerManager.class) {
            if (mController == null) {
                return;
            }
            mController.clearPyFileContent();
        }
    }

    public static void setCellContainer(CellContainer cellContainer) {
        synchronized (ControllerManager.class) {
            containerWeakReference = new WeakReference<>(cellContainer);
        }
    }

    public static Controller getController() {
        synchronized (ControllerManager.class) {
            return mController;
        }
    }

    public static boolean isModified() {
        synchronized (ControllerManager.class) {
            return isModified;
        }
    }

    public static void resetModifyState() {
        synchronized (ControllerManager.class) {
            isModified = false;
        }
    }

    public static WidgetConfig findWidgetConfigById(String id) {
        if (TextUtils.isEmpty(id)) {
            return null;
        }
        synchronized (ControllerManager.class) {
            if (mController == null) {
                return null;
            }
            for (WidgetConfig widgetConfig : mController.configs) {
                if (TextUtils.equals(id, widgetConfig.id)) {
                    return widgetConfig;
                }
            }
            return null;
        }
    }

    public static String findIdBySubscribeId(String subscribeId){
        if (TextUtils.isEmpty(subscribeId)) {
            return null;
        }
        synchronized (ControllerManager.class) {
            if (mController == null) {
                return null;
            }
            for (WidgetConfig widgetConfig : mController.configs) {
                if (TextUtils.equals(subscribeId, widgetConfig.subscribeId)) {
                    return widgetConfig.id;
                }
            }
            return null;
        }
    }

    public static boolean hasRoundJoystick() {
        synchronized (ControllerManager.class) {
            if (mController == null) {
                return false;
            }
            for (WidgetConfig widgetConfig : mController.configs) {
                if (widgetConfig instanceof Joystick4DirectionConfig) {
                    return true;
                }
            }
            return false;
        }
    }

    public static String findMotionNameById(String motionId) {
        Motion motion = Workspace.getInstance().getProject().getMotion(motionId);
        if (motion != null) {
            return motion.name;
        }
        return null;
    }

    public static String generateWidgetNameByMotionId(String widgetType, String motionId, String widgetName) {
        String motionName = ControllerManager.findMotionNameById(motionId);
        if (!TextUtils.isEmpty(motionName)) {
            boolean isNameAvailable =TextUtils.equals(widgetName, motionName)|| ControllerManager.isNameAvailable(widgetType, motionName);
            int index = 0;
            while (!isNameAvailable) {
                index++;
                String indexName = "(" + index + ")" + motionName;
                if (indexName.length() > 20) {
                    indexName = indexName.substring(0, 20);
                }
                isNameAvailable = TextUtils.equals(widgetName, indexName)||ControllerManager.isNameAvailable(widgetType, indexName);
                if (isNameAvailable) {
                    return indexName;
                }
            }
            return motionName;
        } else {
            return widgetName;
        }
    }

    public static boolean isMotionSelectable(String widgetType, String motionId) {
        synchronized (ControllerManager.class) {
            if (mController == null || widgetType == null) {
                return true;
            }
            for (WidgetConfig widgetConfig : mController.configs) {
                if (widgetConfig.type.equals(widgetType) && widgetConfig.mode == WidgetConfig.MODE_EXECUTION_MOTION && !TextUtils.isEmpty(widgetConfig.motionId) && widgetConfig.motionId.equals(motionId)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static boolean isNameAvailable(String type, String name) {
        return isNameAvailable(WidgetType.findWidgetTypeByWidgetName(type), name);
    }

    public static boolean isNameAvailable(WidgetType widgetType, String name) {
        synchronized (ControllerManager.class) {
            if (mController == null || widgetType == null) {
                return false;
            }
            for (WidgetConfig widgetConfig : mController.configs) {
                if (WidgetType.WIDGET_UKTWOWAYJOYSTICKH.equals(widgetType) || WidgetType.WIDGET_UKTWOWAYJOYSTICKV.equals(widgetType)) {
                    if ((TextUtils.equals(WidgetType.WIDGET_UKTWOWAYJOYSTICKH.getWidgetName(), widgetConfig.type)
                            || TextUtils.equals(WidgetType.WIDGET_UKTWOWAYJOYSTICKV.getWidgetName(), widgetConfig.type))
                            && TextUtils.equals(name, widgetConfig.name)) {
                        return false;
                    }
                } else if (WidgetType.WIDGET_UKTOUCHSWITCH.equals(widgetType) || WidgetType.WIDGET_UKTOUCHSWITCH_V2.equals(widgetType)) {
                    if ((TextUtils.equals(WidgetType.WIDGET_UKTOUCHSWITCH.getWidgetName(), widgetConfig.type)
                            || TextUtils.equals(WidgetType.WIDGET_UKTOUCHSWITCH_V2.getWidgetName(), widgetConfig.type))
                            && TextUtils.equals(name, widgetConfig.name)) {
                        return false;
                    }
                } else if (WidgetType.WIDGET_UKCUSTOMBUTTON.equals(widgetType) || WidgetType.WIDGET_UKCUSTOMBUTTON_V2.equals(widgetType)) {
                    if ((TextUtils.equals(WidgetType.WIDGET_UKCUSTOMBUTTON.getWidgetName(), widgetConfig.type)
                            || TextUtils.equals(WidgetType.WIDGET_UKCUSTOMBUTTON_V2.getWidgetName(), widgetConfig.type))
                            && TextUtils.equals(name, widgetConfig.name)) {
                        return false;
                    }
                } else {
                    if (TextUtils.equals(widgetType.getWidgetName(), widgetConfig.type) && TextUtils.equals(name, widgetConfig.name)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    private static String findAvailableName(WidgetType widgetType) {
        String defaultName = widgetType.getDefaultName();
        synchronized (ControllerManager.class) {
            if (mController == null) {
                return defaultName;
            }
            for (int i = 1; ; i++) {
                String name = defaultName + i;
                if (isNameAvailable(widgetType, name)) {
                    return name;
                }
            }
        }
    }

    public static boolean isSubscribeIdAvailable(String subscribeId) {
        synchronized (ControllerManager.class) {
            if (mController == null) {
                return false;
            }
            for (WidgetConfig widgetConfig : mController.configs) {
                if (TextUtils.equals(subscribeId, widgetConfig.subscribeId)) {
                    return false;
                }
            }
            return true;
        }
    }

    private static String findAvailableSubscribeId() {
        synchronized (ControllerManager.class) {
            if (mController == null) {
                return null;
            }
            for (int i = 1; i < 50; i++) {
                String subscribeId = UuidUtil.createUUID().substring(0, 8);
                if (isSubscribeIdAvailable(subscribeId)) {
                    return subscribeId;
                }
            }
            return null;
        }
    }

    public static ControllerBlockly findControllerBlocklyById(String id) {
        if (TextUtils.isEmpty(id)) {
            return null;
        }
        synchronized (ControllerManager.class) {
            if (mController == null) {
                return null;
            }
            for (ControllerBlockly controllerBlockly : mController.blocklies) {
                if (TextUtils.equals(id, controllerBlockly.id)) {
                    return controllerBlockly;
                }
            }
            return null;
        }
    }

    public static String findControllerFileContent(String id, String name) {
        synchronized (ControllerManager.class) {
            ControllerBlockly controllerBlockly = findControllerBlocklyById(id);
            if (controllerBlockly == null) {
                return null;
            }
            for (BlocklyFile blocklyFile : controllerBlockly.blocklyFiles) {
                if (TextUtils.equals(blocklyFile.name, name)) {
                    return blocklyFile.content;
                }
            }
            return null;
        }
    }

    public static void removeWidgetConfigById(String id) {
        removeWidgetConfig(findWidgetConfigById(id));
    }

    private static void removeWidgetConfig(WidgetConfig widgetConfig) {
        if (widgetConfig == null) {
            return;
        }
        synchronized (ControllerManager.class) {
            mController.configs.remove(widgetConfig);
            ControllerBlockly controllerBlockly = findControllerBlocklyById(widgetConfig.blocklyId);
            if (controllerBlockly != null) {
                mController.blocklies.remove(controllerBlockly);
            }
            isModified = true;
        }
    }

    public static void updateWidgetConfig(WidgetConfig newWidgetConfig) {
        updateWidgetConfig(newWidgetConfig, true);
    }

    public static void updateWidgetConfig(WidgetConfig newWidgetConfig, boolean updateName) {
        if (newWidgetConfig == null) {
            return;
        }
        synchronized (ControllerManager.class) {
            if (mController == null) {
                return;
            }
            WidgetConfig oldWidgetConfig = findWidgetConfigById(newWidgetConfig.id);
            if (oldWidgetConfig != null) {
                mController.configs.remove(oldWidgetConfig);
                if (!TextUtils.equals(oldWidgetConfig.blocklyId, newWidgetConfig.blocklyId)) {
                    ControllerBlockly controllerBlockly = findControllerBlocklyById(oldWidgetConfig.blocklyId);
                    mController.blocklies.remove(controllerBlockly);
                }
            } else {
                WidgetType widgetType = WidgetType.findWidgetTypeByWidgetName(newWidgetConfig.type);
                if (updateName) {
                    newWidgetConfig.name = findAvailableName(widgetType);
                    if(newWidgetConfig.subscribeId == null) {
                        newWidgetConfig.subscribeId = findAvailableSubscribeId();
                    }
                }
            }
            mController.configs.add(newWidgetConfig);
            isModified = true;
        }
    }

    public static String setBlocklyContent(String blocklyId, String content) {
        synchronized (ControllerManager.class) {
            if (mController == null) {
                return null;
            }
            ControllerBlockly controllerBlockly = null;
            if (!TextUtils.isEmpty(blocklyId)) {
                controllerBlockly = findControllerBlocklyById(blocklyId);
                if (controllerBlockly != null) {
                    controllerBlockly.blocklyFiles.clear();
                }
            }
            if (controllerBlockly == null) {
                controllerBlockly = new ControllerBlockly();
                if (!TextUtils.isEmpty(blocklyId)) {
                    controllerBlockly.id = blocklyId;
                }
                mController.blocklies.add(controllerBlockly);
            }
            controllerBlockly.workspace = content;
            isModified = true;
            return controllerBlockly.id;
        }
    }

    public static void setBlocklyFileContent(String blocklyId, String name, String content) {
        if (TextUtils.isEmpty(blocklyId)) {
            return;
        }
        synchronized (ControllerManager.class) {
            if (mController == null) {
                return;
            }
            ControllerBlockly controllerBlockly = findControllerBlocklyById(blocklyId);
            if (controllerBlockly == null) {
                return;
            }
            for (BlocklyFile blocklyFile : controllerBlockly.blocklyFiles) {
                if (TextUtils.equals(blocklyFile.name, name)) {
                    controllerBlockly.blocklyFiles.remove(blocklyFile);
                    break;
                }
            }
            BlocklyFile blocklyFile = new BlocklyFile();
            blocklyFile.name = name;
            blocklyFile.content = content;
            controllerBlockly.blocklyFiles.add(blocklyFile);
            isModified = true;
        }
    }

    public static ArrayList<Map<String, String>> findWidgetConfigByType(String widgetType) {
        synchronized (ControllerManager.class) {
            ArrayList<Map<String, String>> result = new ArrayList<>();
            if (mController == null) {
                return result;
            }
            boolean isCustomButton = TextUtils.equals(widgetType, WidgetType.WIDGET_UKCUSTOMBUTTON.getWidgetName());
            for (WidgetConfig widgetConfig : mController.configs) {
                boolean isMatch;
                if (isCustomButton) {
                    isMatch = TextUtils.equals(WidgetType.WIDGET_UKCUSTOMBUTTON_V2.getWidgetName(), widgetConfig.type) || TextUtils.equals(WidgetType.WIDGET_UKCUSTOMBUTTON.getWidgetName(), widgetConfig.type);
                } else {
                    isMatch = TextUtils.equals(widgetType, widgetConfig.type);
                }
                if (isMatch) {
                    Map<String, String> item = new HashMap<>();
                    item.put("id", widgetConfig.id);
                    item.put("name", widgetConfig.name);
                    item.put("subscribeId", widgetConfig.subscribeId);
                    result.add(item);
                }
            }
            return result;
        }
    }

    public static void cleanupCacheValue() {
        synchronized (cacheData) {
            cacheData.clear();
        }
    }

    private static <T> T getCacheValueById(String id, T defaultValue) {
        synchronized (cacheData) {
            T value;
            try {
                value = (T) cacheData.get(id);
                if (value == null) {
                    value = defaultValue;
                }
            } catch (Exception e) {
                value = defaultValue;
            }
            return value;
        }
    }

    private static <T> void updateCacheValueById(String id, T value) {
        synchronized (cacheData) {
            cacheData.put(id, value);
        }
    }

    public static void updateSwitchStatus(String id, boolean value) {
        if (value && ControllerModeHolder.getControllerMode() != ControllerModeHolder.MODE_EXECUTION) {
            return;
        }
        if (getSwitchStatus(id) == value) {
            return;
        }
        if (getBackgroundFlag()) {
            return;
        }
        updateCacheValueById(id, value);
        if (value) {
            executionBlocklyScript(id, BlocklyFile.NAME_ON);
            executionMotion(id);
        } else {
            executionBlocklyScript(id, BlocklyFile.NAME_OFF);
            terminateMotion(id);
        }
    }

    public static void updateButtonStatus(String id, boolean value) {
        if (value && ControllerModeHolder.getControllerMode() != ControllerModeHolder.MODE_EXECUTION) {
            return;
        }
        if (getButtonStatus(id) == value) {
            return;
        }
        if (getBackgroundFlag()) {
            return;
        }
        updateCacheValueById(id, value);
        if (value) {
            executionBlocklyScript(id, BlocklyFile.NAME_DOWN);
            executionMotion(id);
        } else {
            executionBlocklyScript(id, BlocklyFile.NAME_UP);
        }
    }

    public static void updateDefaultSliderStatus(String id, int value) {
        if (ControllerModeHolder.getControllerMode() != ControllerModeHolder.MODE_EXECUTION) {
            return;
        }
        updateCacheValueById(id, value);
    }

    public static void updateSliderStatus(String id, int value) {
        if (ControllerModeHolder.getControllerMode() != ControllerModeHolder.MODE_EXECUTION) {
            return;
        }
        if (getSliderValue(id) == value) {
            return;
        }
        if (getBackgroundFlag()) {
            return;
        }
        updateCacheValueById(id, value);
        executionBlocklyScript(id, BlocklyFile.NAME_CHANGE);
    }

    public static boolean getSwitchStatus(String id) {
        return getCacheValueById(id, false);
    }

    public static boolean getButtonStatus(String id) {
        return getCacheValueById(id, false);
    }

    public static int getSliderValue(String id) {
        return getCacheValueById(id, 0);
    }

    public static void updateReaderColorView(final String id, final int color) {
        if (getBackgroundFlag()) {
            return;
        }
        synchronized (ControllerManager.class) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ReaderColorView view = findReaderColorViewById(id);
                    if (view == null) {
                        return;
                    }
                    view.setColor(color);
                }
            });
        }
    }

    public static void updateReaderValueView(final String id, final int value) {
        if (getBackgroundFlag()) {
            return;
        }
        synchronized (ControllerManager.class) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ReaderValueView view = findReaderValueViewById(id);
                    if (view == null) {
                        return;
                    }
                    view.setValue(value);
                }
            });
        }
    }

    private static ReaderColorView findReaderColorViewById(String id) {
        synchronized (ControllerManager.class) {
            CellContainer cellContainer = containerWeakReference.get();
            if (cellContainer == null) {
                return null;
            }
            int count = cellContainer.getChildCount();
            for (int i = 0; i < count; i++) {
                View view = cellContainer.getChildAt(i);
                if (view instanceof ReaderColorView && TextUtils.equals(((ReaderColorView) view).getWidgetConfig().id, id)) {
                    return (ReaderColorView) view;
                }
            }
            return null;
        }
    }

    private static ReaderValueView findReaderValueViewById(String id) {
        synchronized (ControllerManager.class) {
            CellContainer cellContainer = containerWeakReference.get();
            if (cellContainer == null) {
                return null;
            }
            int count = cellContainer.getChildCount();
            for (int i = 0; i < count; i++) {
                View view = cellContainer.getChildAt(i);
                if (view instanceof ReaderValueView && TextUtils.equals(((ReaderValueView) view).getWidgetConfig().id, id)) {
                    return (ReaderValueView) view;
                }
            }
            return null;
        }
    }

    private static ButtonCustomView findButtonCustomViewById(String id) {
        synchronized (ControllerManager.class) {
            CellContainer cellContainer = containerWeakReference.get();
            if (cellContainer == null) {
                return null;
            }
            int count = cellContainer.getChildCount();
            for (int i = 0; i < count; i++) {
                View view = cellContainer.getChildAt(i);
                if (view instanceof ButtonCustomView && TextUtils.equals(((ButtonCustomView) view).getWidgetConfig().id, id)) {
                    return (ButtonCustomView) view;
                }
            }
            return null;
        }
    }

    private static ButtonCustomViewV2 findButtonCustomViewV2ById(String id) {
        synchronized (ControllerManager.class) {
            CellContainer cellContainer = containerWeakReference.get();
            if (cellContainer == null) {
                return null;
            }
            int count = cellContainer.getChildCount();
            for (int i = 0; i < count; i++) {
                View view = cellContainer.getChildAt(i);
                if (view instanceof ButtonCustomViewV2 && TextUtils.equals(((ButtonCustomViewV2) view).getWidgetConfig().id, id)) {
                    return (ButtonCustomViewV2) view;
                }
            }
            return null;
        }
    }

    private static void resetAllViewState() {
        synchronized (ControllerManager.class) {
            if (containerWeakReference == null) {
                return;
            }
            CellContainer cellContainer = containerWeakReference.get();
            if (cellContainer == null) {
                return;
            }
            int count = cellContainer.getChildCount();
            for (int i = 0; i < count; i++) {
                View view = cellContainer.getChildAt(i);
                if (view instanceof ControllerWidgetView) {
                    ((ControllerWidgetView) view).resetState();
                }
            }
        }
    }

    public static void setBackgroundFlag(boolean flag) {
        synchronized (ControllerManager.class) {
            backgroundFlag = flag;
        }
    }

    public static boolean getBackgroundFlag() {
        synchronized (ControllerManager.class) {
            return backgroundFlag;
        }
    }

    public static void terminateExecution() {
        terminateExecution(true,false);
    }

    public static void terminateExecution(final boolean stopCollector, boolean restart) {
        BluetoothCommunicator.getInstance().cleanupAllInvocationSequence();
        handler.removeCallbacks(widgetDefaultValueRunnable);
        cleanupCacheValue();
        synchronized (cacheJoystick) {
            cacheJoystick.clear();
        }
        synchronized (sliderRunnableMap) {
            for (Runnable runnable : sliderRunnableMap.values()) {
                handler.removeCallbacks(runnable);
            }
            sliderRunnableMap.clear();
        }
        synchronized (cacheExecution) {
            if (stopCollector) {
                PeripheralErrorCollector.getInstance().updateCollectorState(PeripheralErrorCollector.CollectorState.STOP, null);
            }
            Collection<ExecutionData> items = cacheExecution.values();
            for (ExecutionData item : items) {
                item.terminate();
            }
            cacheExecution.clear();
            updateExecutionStatus();
        }
        if (isSmartDevice()){//2.0主控
            cancelRestartPython();
            if (PyScriptRunningStateHolder.getInstance().isScriptStarted()) {//startPython后，通过点击停止暂停按钮、锁屏、外设异常调用terminateExecution()的情况下，才需要发送停止脚本指令
                if (restart) {//灰色暂停按钮流程
                    restartPython();
                } else {
                    setScriptRestarting(false);
                    notifyPauseExecutionEnd();
                    PyScriptRunningStateHolder.getInstance().setScriptStopped();//发生stopPython更新脚本stop状态，则在CommonPushMessageEventHandler不处理本次停止消息，以免退出EXECUTION_MODE
                    stopPythonScript();
                }
            }
        }else{//1.0主控
            MediaAudioPlayer.getInstance().stopAudio();
            BluetoothHelper.stopRobot();
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                resetAllViewState();
            }
        });
    }
    private static URoMissionAbortSignal restartPythonSignal=null;
    public static void cancelRestartPython(){
        if(!isSmartDevice()) {
            return;
        }
        if (restartPythonSignal!=null&&!restartPythonSignal.isAbort()){
            restartPythonSignal.abort();
            restartPythonSignal=null;
        }
    }

    public static void restartPython(){
        if (!isSmartDevice()) {
            return;
        }
        notifyPauseExecutionStart();
        setScriptRestarting(true);
        URoInvocationSequence sequence = new URoInvocationSequence();
        sequence.action(BtInvocationFactory.cleanupScriptValue(null));
        sequence.action(BtInvocationFactory.restartScript(null));
        sequence.action(BtInvocationFactory.stopRunning(null));//放在最后，否则外设停不下来
        sequence.sleep(250);
        HashMap<String, String> widgetDefaultValue = getWidgetDefaultValue();
        if (widgetDefaultValue != null && !widgetDefaultValue.isEmpty()) {
            sequence.action(BtInvocationFactory.updateScriptValues(widgetDefaultValue, null));
        }
        sequence.setCompletionCallback(new URoMissionCallback() {
            @Override
            public void onMissionNextStep(int currentStep, int totalStep) {

            }

            @Override
            public void onMissionBegin() {
                restartPythonSignal=getAbortSignal();
            }

            @Override
            public void onMissionEnd() {
                restartPythonSignal=null;
                setScriptRestarting(false);
                notifyPauseExecutionEnd();
            }

            @Override
            public void onComplete(URoCompletionResult result) {
            }
        });
        boolean resultFlag = BluetoothHelper.addCommand(sequence);
        if (!resultFlag) {
            setScriptRestarting(false);
            notifyPauseExecutionEnd();
        }
    }

    private static ExecutionData getExecutionById(String id) {
        synchronized (cacheExecution) {
            return cacheExecution.get(id);
        }
    }

    public static void updateJoystickStatus(String id, boolean status) {
        if (status && ControllerModeHolder.getControllerMode() != ControllerModeHolder.MODE_EXECUTION) {
            return;
        }
        if (getBackgroundFlag()) {
            return;
        }
        synchronized (cacheJoystick) {
            cacheJoystick.put(id, status);
            updateExecutionStatus();
        }
    }

    public static void addExecutionStatusChangeListener(IExecutionStatusChangeListener listener) {
        synchronized (listeners) {
            if (listener == null) {
                return;
            }
            listeners.add(listener);
        }
    }

    public static void removeExecutionStatusChangeListener(IExecutionStatusChangeListener listener) {
        synchronized (listeners) {
            if (listener == null) {
                return;
            }
            listeners.remove(listener);
        }
    }

    private static void notifyExecutionStatusChanged(final boolean hasExecution) {
        synchronized (listeners) {
            HashSet<IExecutionStatusChangeListener> list = new HashSet<>(listeners);
            for (final IExecutionStatusChangeListener l : list) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (l != null) {
                            l.onExecutionStatusChanged(hasExecution);
                        }
                    }
                });
            }
        }
    }


    private static void notifyPauseExecutionStart() {
        synchronized (listeners) {
            HashSet<IExecutionStatusChangeListener> list = new HashSet<>(listeners);
            for (final IExecutionStatusChangeListener l : list) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (l != null) {
                            l.onPauseExecutionStart();
                        }
                    }
                });
            }
        }
    }

    private static void notifyPauseExecutionEnd() {
        synchronized (listeners) {
            HashSet<IExecutionStatusChangeListener> list = new HashSet<>(listeners);
            for (final IExecutionStatusChangeListener l : list) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (l != null) {
                            l.onPauseExecutionEnd();
                        }
                    }
                });
            }
        }
    }

    private static void updateExecutionStatus() {
        synchronized (cacheExecution) {
            synchronized (cacheJoystick) {
                boolean newExecutionFlag = !cacheExecution.isEmpty() || cacheJoystick.values().contains(Boolean.TRUE);
                if (newExecutionFlag != executionFlag) {
                    executionFlag = newExecutionFlag;
                    notifyExecutionStatusChanged(executionFlag);
                }
            }
        }
    }

    private static boolean submitNewExecution(String id, LuaExecutor luaExecutor, ActionSequenceProcessListener listener) {
        return submitNewExecution(id, null, luaExecutor, listener);
    }

    private static boolean submitNewExecution(String id, URoInvocationSequence bas, ActionSequenceProcessListener listener) {
        return submitNewExecution(id, bas, null, listener);
    }

    private static boolean submitNewExecution(String id, URoInvocationSequence bas, LuaExecutor luaExecutor, ActionSequenceProcessListener listener) {
        synchronized (cacheExecution) {
            if (TextUtils.isEmpty(id) || (bas == null && luaExecutor == null)) {
                return false;
            }
            ExecutionData executionData = getExecutionById(id);
            if (executionData != null) {
                executionData.terminate();
            } else {
                executionData = new ExecutionData();
                cacheExecution.put(id, executionData);
                updateExecutionStatus();
            }
            executionData.timestamp = System.currentTimeMillis();
            final ExecutionData finalExecution = executionData;
            if (bas != null) {
//                return bas.execute();
                bas.setCompletionCallback(new ErrorCollectSequenceCallback(bas) {
                    @Override
                    public void onMissionBegin() {
                        finalExecution.bas = getAbortSignal();
                        if (listener != null) {
                            listener.onProcessBegin();
                        }
                    }

                    @Override
                    public void onMissionEnd() {
                        if (listener != null) {
                            listener.onProcessEnd(getAbortSignal());
                        }
                    }

                    @Override
                    public void onComplete(URoCompletionResult result) {
                    }
                });
                //fixme 是否需要返回值
                BluetoothHelper.addCommand(bas);
                return true;
            } else {
                executionData.le = luaExecutor;
                return luaExecutor.execute();
            }
        }
    }

    private static boolean executionBlocklyScript(String id, String name) {
        WidgetConfig widgetConfig = findWidgetConfigById(id);
        if (widgetConfig == null) {
            return false;
        }
        if(isSmartDevice()) {
            String subscribeId = findSubscribeIdById(id);
            if(TextUtils.isEmpty(subscribeId)) {
                return false;
            }
            if(TextUtils.equals(BlocklyFile.NAME_CHANGE, name)) {
                Runnable runnable;
                synchronized (sliderRunnableMap) {
                    runnable = sliderRunnableMap.remove(id);
                }
                if(runnable != null) {
                    handler.removeCallbacks(runnable);
                }
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        synchronized (sliderRunnableMap) {
                            sliderRunnableMap.remove(id);
                        }
                        int sliderValue = getSliderValue(id);
                        String value = String.valueOf(sliderValue);
                        BluetoothHelper.addCommand(BtInvocationFactory.updateScriptValue(subscribeId, value, null));
                        BluetoothHelper.addCommand(BtInvocationFactory.updateScriptEvent(subscribeId, SCRIPT_EVENT_CHANGE, null));
                    }
                };
                synchronized (sliderRunnableMap) {
                    sliderRunnableMap.put(id, runnable);
                }
                handler.postDelayed(runnable, 100);
            } else {
                String value = null;
                String event = null;
                if(TextUtils.equals(BlocklyFile.NAME_DOWN, name)) {
                    value = SCRIPT_VALUE_DOWN;
                    event = SCRIPT_EVENT_DOWN;
                } else if(TextUtils.equals(BlocklyFile.NAME_UP, name)) {
                    value = SCRIPT_VALUE_UP;
                    event = SCRIPT_EVENT_UP;
                } else if(TextUtils.equals(BlocklyFile.NAME_ON, name)) {
                    value = SCRIPT_VALUE_ON;
                    event = SCRIPT_EVENT_ON;
                } else if(TextUtils.equals(BlocklyFile.NAME_OFF, name)) {
                    value = SCRIPT_VALUE_OFF;
                    event = SCRIPT_EVENT_OFF;
                }
                if(!TextUtils.isEmpty(value)) {
                    BluetoothHelper.addCommand(BtInvocationFactory.updateScriptValue(subscribeId, value, null));
                    BluetoothHelper.addCommand(BtInvocationFactory.updateScriptEvent(subscribeId, event, null));
                }
            }
            return true;
        } else {
            if (widgetConfig instanceof ButtonCustomConfig && ((ButtonCustomConfig) widgetConfig).getMode() != WidgetConfig.MODE_EXECUTION_BLOCKLY) {
                return false;
            }
            if (widgetConfig instanceof SwitchToggleConfig && ((SwitchToggleConfig) widgetConfig).getMode() != WidgetConfig.MODE_EXECUTION_BLOCKLY) {
                return false;
            }
            if (widgetConfig instanceof SwitchTouchConfig && ((SwitchTouchConfig) widgetConfig).getMode() != WidgetConfig.MODE_EXECUTION_BLOCKLY) {
                return false;
            }
            if (TextUtils.isEmpty(widgetConfig.blocklyId)) {
                return false;
            }
            String luaContent = findControllerFileContent(widgetConfig.blocklyId, name);
            if (luaContent == null) {
                return false;
            }
            LuaExecutor luaExecutor = LuaExecutor.newLuaExecutor();
            luaExecutor.loadFromString(String.format(ControllerConstValue.LUA_FILE_TEMPLATE, luaContent));
            luaExecutor.editDone();
            luaExecutor.setLuaExecutorCallback(new LuaCallback(id));
            return submitNewExecution(id, luaExecutor, null);
        }
    }

    private static boolean executionMotion(String id) {
        if (!BluetoothHelper.isConnected()) {
            return false;
        }
        if (Workspace.getInstance() == null || Workspace.getInstance().getProject() == null) {
            return false;
        }
        WidgetConfig widgetConfig = findWidgetConfigById(id);
        if (widgetConfig == null) {
            return false;
        }
        if (widgetConfig instanceof ButtonCustomConfig && ((ButtonCustomConfig) widgetConfig).getMode() != WidgetConfig.MODE_EXECUTION_MOTION) {
            return false;
        }
        if (widgetConfig instanceof SwitchToggleConfig && ((SwitchToggleConfig) widgetConfig).getMode() != WidgetConfig.MODE_EXECUTION_MOTION) {
            return false;
        }
        if (widgetConfig instanceof SwitchTouchConfig && ((SwitchTouchConfig) widgetConfig).getMode() != WidgetConfig.MODE_EXECUTION_MOTION) {
            return false;
        }
        if (TextUtils.isEmpty(widgetConfig.motionId)) {
            return false;
        }
        if (widgetConfig instanceof ButtonCustomConfig) {
            ExecutionData executionData = getExecutionById(id);
            //fixme 序列结束标识 是否使用getAbortSignal
            if (executionData != null && executionData.bas != null && !executionData.bas.isAbort()) {
                return false;
            }
        }
        Motion motion = Workspace.getInstance().getProject().getMotion(widgetConfig.motionId);
        if (motion == null) {
            if (BluetoothHelper.isConnected()) { //在蓝牙连接后，才会弹出动作文件被删除的提示
                terminateExecution();
                BluetoothCommonErrorHelper.openBluetoothCommonErrorActivity(BluetoothCommonErrorHelper.CommonError.MOTION_DELETE);
            }
            return false;
        }

//        UKitDeviceActionSequence bas = UKitDeviceActionSequenceFactory.createMotionActionSequence(motion, true, null);
//        ActionSequenceProcessListener listener = null;
//        if (widgetConfig instanceof SwitchToggleConfig || widgetConfig instanceof SwitchTouchConfig) {
//            bas.loop(true);
//            listener = new ActionSequenceProcessListener(id, false, 0);
//            bas.listener(listener);
//        } else if (widgetConfig instanceof ButtonCustomConfig) {
//            listener = new ActionSequenceProcessListener(id, true, motion.totaltime);
//            bas.listener(listener);
//        }
//        if (bas.getActionCount() == 0) {
//            if (listener != null) {
//                listener.onProcessEnd(bas);
//            }
//            return false;
//        }
//        return submitNewExecution(id, bas);

        URoInvocationSequence uRoInvocationSequence = BtInvocationFactory.exeMotion(motion);
        ActionSequenceProcessListener listener = null;
        if (widgetConfig instanceof SwitchToggleConfig || widgetConfig instanceof SwitchTouchConfig) {
            listener = new ActionSequenceProcessListener(id, false, 0);
            uRoInvocationSequence.loop(true);
        } else if (widgetConfig instanceof ButtonCustomConfig) {
            listener = new ActionSequenceProcessListener(id, true, motion.totaltime);
        }
        return submitNewExecution(id, uRoInvocationSequence, listener);
    }

    private static void terminateExecution(String id) {
        terminateExecution(id, null, null);
    }

    private static void terminateExecution(String id, LuaExecutor le, URoMissionAbortSignal bas) {
        synchronized (cacheExecution) {
            if (TextUtils.isEmpty(id)) {
                return;
            }
            ExecutionData executionData = cacheExecution.get(id);
            if (executionData != null && executionData.terminate(bas, le)) {
                cacheExecution.remove(id);
                updateExecutionStatus();
            }
        }
    }

    private static void terminateMotion(String id) {
        WidgetConfig widgetConfig = findWidgetConfigById(id);
        if (widgetConfig == null) {
            return;
        }
        if (widgetConfig instanceof ButtonCustomConfig && ((ButtonCustomConfig) widgetConfig).getMode() != WidgetConfig.MODE_EXECUTION_MOTION) {
            return;
        }
        if (widgetConfig instanceof SwitchToggleConfig && ((SwitchToggleConfig) widgetConfig).getMode() != WidgetConfig.MODE_EXECUTION_MOTION) {
            return;
        }
        if (widgetConfig instanceof SwitchTouchConfig && ((SwitchTouchConfig) widgetConfig).getMode() != WidgetConfig.MODE_EXECUTION_MOTION) {
            return;
        }
        terminateExecution(id);
    }

    private static String findSubscribeIdById(String id) {
        WidgetConfig widgetConfig = findWidgetConfigById(id);
        if (widgetConfig == null) {
            return null;
        }
        return widgetConfig.subscribeId;
    }

    private static class LuaCallback implements LuaExecutor.LuaExecutorCallback {
        private String id;

        public LuaCallback(String id) {
            this.id = id;
        }

        @Override
        public void onLuaExecuteBegin(LuaExecutor le) {
//            RcLogUtils.e("onLuaExecuteBegin");
        }

        @Override
        public void onLuaExecuteError(LuaExecutor le, Throwable e) {
//            RcLogUtils.e("onLuaExecuteError", e);
            if (e instanceof StackOverflowError) {
                ExecutionData executionData = cacheExecution.get(id);
                if (executionData != null) {
                    if (le.equals(executionData.le)) {
                        BluetoothHelper.getBtHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                String msg = UKitApplication.getInstance().getString(R.string.project_controller_stack_overflow_msg);
                                ToastHelper.toastShort(msg);
                                ControllerManager.terminateExecution();
                                PeripheralErrorCollector.getInstance().updateCollectorState(PeripheralErrorCollector.CollectorState.STOP, null);
                            }
                        });
                    }
                }
            }
        }

        @Override
        public void onLuaExecuteEnd(LuaExecutor le) {
//            RcLogUtils.e("onLuaExecuteEnd");
            terminateExecution(id, le, null);
        }
    }

    //    private static class ActionSequenceProcessListener implements IActionSequenceProcessListener {
    private static class ActionSequenceProcessListener {
        private String id;
        private ButtonCustomView view;
        private ButtonCustomViewV2 view2;
        private long totalTime;

        public ActionSequenceProcessListener(String id, boolean buttonView, long totalTime) {
            this.id = id;
            if (buttonView) {
                WidgetConfig widgetConfig = findWidgetConfigById(id);
                if (widgetConfig instanceof ButtonCustomConfigV2) {
                    view2 = findButtonCustomViewV2ById(id);
                } else if (widgetConfig instanceof ButtonCustomConfig) {
                    view = findButtonCustomViewById(id);
                }
                this.totalTime = totalTime;
            }
        }

        //        @Override
//        public void onProcessChanged(int currentIndex, int totalAction) {
////            RcLogUtils.e("onProcessChanged");
//            UKitDeviceActionSequence.ActionParameter actionParameter = bas.getNextActionParameter();
//            if(!(actionParameter instanceof UKitDeviceActionSequence.CommandActionParameter)) {
//                return;
//            }
//            UKitDeviceActionSequence.CommandActionParameter commandActionParameter = (UKitDeviceActionSequence.CommandActionParameter)actionParameter;
//            UKitCommandRequest command = commandActionParameter.getCommand();
//            if(!(command instanceof UKitCmdSteeringGearAngle)) {
//                return;
//            }
//            UKitCmdSteeringGearAngle steeringGearAngle = (UKitCmdSteeringGearAngle)command;
//            ArrayList<Integer> ids = steeringGearAngle.getIdList();
//            if(ids == null || ids.isEmpty()) {
//                return;
//            }
//            for(Integer id : ids) {
//                PeripheralErrorCollector.getInstance().appendUsedPeripheral(UKitPeripheralType.PERIPHERAL_STEERING_GEAR, id);
//            }
//        }
//
//        @Override
//        public void onProcessBegin(UKitDeviceActionSequence bas) {
////            RcLogUtils.e("onProcessBegin");
//            if (view2 != null) {
//                view2.startAnimator(totalTime);
//            } else if (view != null) {
//                view.startAnimator(totalTime);
//            }
//        }
//
//        @Override
//        public void onProcessEnd(UKitDeviceActionSequence bas) {
////            RcLogUtils.e("onProcessEnd");
//            if (view2 != null) {
//                view2.stopAnimator();
//            } else if (view != null) {
//                view.stopAnimator();
//            }
//            terminateExecution(id, null, bas);
//        }


        public void onProcessBegin() {
//            RcLogUtils.e("onProcessBegin");
            if (view2 != null) {
                view2.startAnimator(totalTime);
            } else if (view != null) {
                view.startAnimator(totalTime);
            }
        }

        public void onProcessEnd(URoMissionAbortSignal bas) {
//            RcLogUtils.e("onProcessEnd");
            if (view2 != null) {
                view2.stopAnimator();
            } else if (view != null) {
                view.stopAnimator();
            }
            terminateExecution(id, null, bas);
        }
    }

    public static void addScripExecEvent(String subscribeId){
        String id = findIdBySubscribeId(subscribeId);
        if (TextUtils.isEmpty(id)) {
            return;
        }
        synchronized (cacheExecution) {
            if (!cacheExecution.containsKey(id)) {
                ExecutionData executionData=new ExecutionData();
                executionData.timestamp=System.currentTimeMillis();
                cacheExecution.put(id, executionData);
                updateExecutionStatus();
            }
        }
    }

    public static void removeScripExecEvent(String subscribeId){
        String id = findIdBySubscribeId(subscribeId);
        if (TextUtils.isEmpty(id)) {
            return;
        }
        synchronized (cacheExecution) {
            if (cacheExecution.containsKey(id)) {
                cacheExecution.remove(id);
                updateExecutionStatus();
            }
        }
    }
}
