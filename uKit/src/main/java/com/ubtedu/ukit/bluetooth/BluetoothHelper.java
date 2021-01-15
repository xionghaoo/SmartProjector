package com.ubtedu.ukit.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.component.URoBrightnessSensor;
import com.ubtedu.deviceconnect.libs.base.component.URoColorSensor;
import com.ubtedu.deviceconnect.libs.base.component.URoComponent;
import com.ubtedu.deviceconnect.libs.base.component.URoEnvironmentSensor;
import com.ubtedu.deviceconnect.libs.base.component.URoInfraredSensor;
import com.ubtedu.deviceconnect.libs.base.component.URoSensorComponent;
import com.ubtedu.deviceconnect.libs.base.component.URoSoundSensor;
import com.ubtedu.deviceconnect.libs.base.component.URoSpeaker;
import com.ubtedu.deviceconnect.libs.base.component.URoTouchSensor;
import com.ubtedu.deviceconnect.libs.base.component.URoUltrasoundSensor;
import com.ubtedu.deviceconnect.libs.base.connector.URoDiscoveryCallback;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoBatteryChangeListener;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoComponentErrorListener;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoConnectStatusChangeListener;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoPushMessageReceivedListener;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocation;
import com.ubtedu.deviceconnect.libs.base.model.URoBatteryInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoColor;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentID;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoHumitureValue;
import com.ubtedu.deviceconnect.libs.base.model.URoMainBoardInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoSerialNumberInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoTouchSensorValue;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.deviceconnect.libs.base.product.URoProductManager;
import com.ubtedu.deviceconnect.libs.ukit.connector.URoUkitBluetoothConnector;
import com.ubtedu.deviceconnect.libs.ukit.connector.URoUkitBluetoothInfo;
import com.ubtedu.deviceconnect.libs.ukit.connector.UroSequenceConnectMissionCallback;
import com.ubtedu.deviceconnect.libs.ukit.connector.UroUkitBluetoothSequenceConnector;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoWiFiStatusInfo;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.bluetooth.connect.BluetoothConnectActivity;
import com.ubtedu.ukit.bluetooth.interfaces.INetworkStateChangedListener;
import com.ubtedu.ukit.bluetooth.interfaces.IWiFiStateInfoChangedListener;
import com.ubtedu.ukit.bluetooth.model.SensorValueData;
import com.ubtedu.ukit.bluetooth.processor.CommonBatteryEventHandler;
import com.ubtedu.ukit.bluetooth.processor.CommonBoardNetworkStateHolder;
import com.ubtedu.ukit.bluetooth.processor.CommonConnectEventHandler;
import com.ubtedu.ukit.bluetooth.processor.CommonPushMessageEventHandler;
import com.ubtedu.ukit.bluetooth.processor.PeripheralErrorCollector;
import com.ubtedu.ukit.bluetooth.search.BluetoothSearchActivity;
import com.ubtedu.ukit.bluetooth.utils.BtLogUtils;
import com.ubtedu.ukit.project.bridge.BluetoothCommunicator;
import com.ubtedu.ukit.project.bridge.BridgeCommunicator;
import com.ubtedu.ukit.project.bridge.MotionDesigner;
import com.ubtedu.ukit.project.bridge.functions.BlocklyFunctions;
import com.ubtedu.ukit.project.bridge.functions.Unity3DFunctions;
import com.ubtedu.ukit.project.controller.ControllerModeHolder;
import com.ubtedu.ukit.project.controller.manager.ControllerManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @Author naOKi
 **/
public class BluetoothHelper {

    private BluetoothHelper() {
    }

    private static final BluetoothAdapter adapter;

    private static final Handler btHandler;

    private static URoUkitBluetoothInfo connectDevice;

    static {
        adapter = BluetoothAdapter.getDefaultAdapter();
        btHandler = new Handler(Looper.getMainLooper());
        BluetoothHelper.addConnectStatusChangeListener(CommonConnectEventHandler.getInstance());
        BluetoothHelper.addBatteryChangeListener(CommonBatteryEventHandler.getInstance());
        BluetoothHelper.addPushMessageReceivedListener(CommonPushMessageEventHandler.getInstance());
        BluetoothHelper.addConnectStatusChangeListener(CommonBoardNetworkStateHolder.getInstance());
        PeripheralErrorCollector.register();

        URoProductManager.getInstance().setProductManagerDelegate(UkitDeviceCompact.getInstance());
    }

    public static UKitApplication getApp() {
        return UKitApplication.getInstance();
    }

    public static Handler getBtHandler() {
        return btHandler;
    }

    public static void enable() {
        if (adapter != null && !adapter.isEnabled()) {
            adapter.enable();
        }
    }

    public static void disable() {
        if (adapter != null && adapter.isEnabled()) {
            adapter.disable();
        }
    }

    public static void openActivity(Fragment fragment) {
        openActivity(fragment.getContext());
    }

    public static void openActivity(Activity activity) {
        openActivity(activity);
    }

    private static void openActivity(Context context) {
        if (isConnected()) {
            BluetoothConnectActivity.entryBluetoothConnectActivity(context, BluetoothConnectActivity.ENTRY_MODE_CONNECTED, true, true);
        } else {
            Intent intent = new Intent(context, BluetoothSearchActivity.class);
            context.startActivity(intent);
        }
    }

    public static void setVerification(boolean isVerification) {
        if (UkitDeviceCompact.getInstance().getDefaultDevice() == null) {
            return;
        }
        UkitDeviceCompact.getInstance().setVerified(isVerification);
    }

    public static boolean isVerification() {
        if (UkitDeviceCompact.getInstance().getDefaultDevice() == null) {
            return false;
        }
        return UkitDeviceCompact.getInstance().isVerified();
    }

    public static boolean isConnected() {
        URoProduct device = UkitDeviceCompact.getInstance().getDefaultDevice();
        if (device == null) {
            return false;
        }
        return device.isConnected() && isVerification();
    }

    public static boolean isBluetoothConnected() {
        URoProduct device = UkitDeviceCompact.getInstance().getDefaultDevice();
        if (device == null) {
            return false;
        }
        return device.isConnected();
    }

    public static boolean isEnabled() {
        return adapter != null && adapter.isEnabled();
    }

    public static void startScan(URoDiscoveryCallback<URoUkitBluetoothInfo> callback) {
        startScan(callback, 15000);
    }

    public static void startScan(URoDiscoveryCallback<URoUkitBluetoothInfo> callback, long timeoutMs) {
        stopScan();
        URoUkitBluetoothConnector.getInstance().startSearch(callback, timeoutMs);
    }

    public static void stopScan() {
        if (URoUkitBluetoothConnector.getInstance().isSearching()) {
            URoUkitBluetoothConnector.getInstance().stopSearch();
        }
    }

    public static void connect(URoUkitBluetoothInfo connectItem) {
        connect(connectItem, null);
    }

    public static void connect(URoUkitBluetoothInfo connectItem, final URoCompletionCallback<Void> listener) {
        if (connectItem == null) {
            return;
        }
        connectDevice = connectItem;
        URoUkitBluetoothConnector.getInstance().connect(connectItem, 15000, listener);
    }

    public static void connectDevicesWithWifi(ArrayList<URoUkitBluetoothInfo> devices, String wifiSsid, String password, UroSequenceConnectMissionCallback<URoWiFiStatusInfo> callback) {
        UroUkitBluetoothSequenceConnector.getInstance().connectDevicesWithInitWifi(devices, wifiSsid, password, callback);
    }

    public static URoUkitBluetoothInfo getConnectDevice() {
        return connectDevice;
    }

    public static boolean disconnect() {
        URoProduct device = UkitDeviceCompact.getInstance().getDefaultDevice();
        if (device == null) {
            return false;
        }
        return device.disconnect();
    }

    public static boolean addCommand(URoInvocation invocation) {
        return addCommand(invocation, null, null);
    }

    public static boolean addCommand(URoInvocation invocation, UKitCommandPriority priority, String tag) {
        URoProduct device = UkitDeviceCompact.getInstance().getDefaultDevice();
        if (invocation == null || device == null) {
            return false;
        }
        if (tag != null) {
            invocation.setTag(tag);
        }
        return invocation.sendToTarget(device);
    }

    public static void removeByTag(Object tag) {
//        if (UkitDeviceCompact.getInstance().getDefaultDevice() == null) {
//            return;
//        }
//        UkitDeviceCompact.getInstance().getDefaultDevice().removeByTag(tag);
    }

    public static void terminateExecution() {
        terminateExecution(true);
    }

    public static void terminateExecution(boolean forced) {
        try {
            //BtLogUtils.e("terminateExecution: %s", PeripheralErrorCollector.getInstance().toString());
            if (ControllerModeHolder.getControllerMode() == ControllerModeHolder.MODE_EXECUTION) {
                //强行停止遥控器
                ControllerManager.terminateExecution();
                PeripheralErrorCollector.getInstance().updateCollectorState(PeripheralErrorCollector.CollectorState.STOP, null);
            } else if (PeripheralErrorCollector.getInstance().isCurrentType(PeripheralErrorCollector.ErrorCollectorType.BLOCKLY)) {
                BluetoothCommunicator.getInstance().cancelSendPython();
                //强行停止Blockly
                if (BridgeCommunicator.getInstance().getBlocklyBridge(false).isCommunicable()) {
                    BridgeCommunicator.getInstance().getBlocklyBridge(false).call(BlocklyFunctions.terminateExecProgram, null, null);
                }
                if (BridgeCommunicator.getInstance().getBlocklyBridge(true).isCommunicable()) {
                    BridgeCommunicator.getInstance().getBlocklyBridge(true).call(BlocklyFunctions.terminateExecProgram, new Object[]{false}, null);//参数{false}表示不要跳过stopPython
                }
                PeripheralErrorCollector.getInstance().updateCollectorState(PeripheralErrorCollector.CollectorState.STOP, null);
            } else if (PeripheralErrorCollector.getInstance().isCurrentType(PeripheralErrorCollector.ErrorCollectorType.MOTION_RECORDING)) {
                //强行停止Unity回读
                MotionDesigner.getInstance().stopAutoProgram();
                if (BridgeCommunicator.getInstance().getUnity3DBridge().isCommunicable()) {
                    if (forced) {
                        BridgeCommunicator.getInstance().getUnity3DBridge().call(Unity3DFunctions.terminatePlayback, null, null);
                    } else {
                        BridgeCommunicator.getInstance().getUnity3DBridge().call(Unity3DFunctions.stopRecording, null, null);
                    }
                }
                PeripheralErrorCollector.getInstance().updateCollectorState(PeripheralErrorCollector.CollectorState.STOP, forced ? null : PeripheralErrorCollector.ErrorCollectorType.MOTION_RECORDING);
            } else if (PeripheralErrorCollector.getInstance().isCurrentType(PeripheralErrorCollector.ErrorCollectorType.MOTION_PREVIEW)) {
                //强行停止Unity预览
                if (BridgeCommunicator.getInstance().getUnity3DBridge().isCommunicable()) {
                    BridgeCommunicator.getInstance().getUnity3DBridge().call(Unity3DFunctions.terminatePlayback, null, null);
                }
                PeripheralErrorCollector.getInstance().updateCollectorState(PeripheralErrorCollector.CollectorState.STOP, null);
            }
        } catch (Exception e) {
            BtLogUtils.e(e);
        } finally {
            BluetoothHelper.stopRobot();
        }
    }

    public static void stopRobot() {
        URoProduct device = UkitDeviceCompact.getInstance().getDefaultDevice();
        if (device == null) {
            return;
        }
        device.stopRunning(null);
    }

    public static void setDeviceName(String deviceName) {
//        if (UkitDeviceCompact.getInstance().getDefaultDevice() == null) {
//            return;
//        }
//        UkitDeviceCompact.getInstance().getDefaultDevice().setDeviceName(deviceName);
    }

    public static String getDeviceName() {
        URoProduct device = UkitDeviceCompact.getInstance().getDefaultDevice();
        if (device == null) {
            return null;
        }
        return formatDeviceName(device.getName());
    }

    public static String formatDeviceName(String deviceName) {
        String name;
        if (!TextUtils.isEmpty(deviceName) && deviceName.startsWith("uKit2_")) {
            name = deviceName;
        } else if (TextUtils.equals("jimu", deviceName.toLowerCase())) {
            name = "uKit";//格式化名字 Jimu -> uKit
        } else {
            name = "uKit_" + deviceName.substring(5).toUpperCase();//格式化名字 Jimu_XXXX -> uKit_XXXX
        }
        return name;
    }


    public static URoSpeaker getSpeaker() {
        URoProduct device = UkitDeviceCompact.getInstance().getDefaultDevice();
        if (device == null) {
            return null;
        }
        return device.getSpeaker();
    }

    public static URoSerialNumberInfo getSerialNumberData() {
        URoProduct device = UkitDeviceCompact.getInstance().getDefaultDevice();
        if (device == null) {
            return null;
        }
        return device.getSerialNumberInfo();
    }

    public static URoMainBoardInfo getBoardInfoData() {
        return UkitDeviceCompact.getInstance().getMainBoardInfo();
    }

    public static ArrayList<URoComponentID> getComponents() {
        URoProduct device = UkitDeviceCompact.getInstance().getDefaultDevice();
        if (device == null) {
            return null;
        }
        ArrayList<URoComponent> components = device.getComponents();
        if (components == null || components.size() == 0) {
            return null;
        }
        ArrayList<URoComponentID> componentIDS = new ArrayList<>();
        for (int i = 0; i < components.size(); i++) {
            componentIDS.add(new URoComponentID(components.get(i).getComponentType(), components.get(i).getComponentId()));
        }
        return componentIDS;
    }

    public static int[] getComponentByType(URoComponentType componentType) {
        URoProduct device = UkitDeviceCompact.getInstance().getDefaultDevice();
        if (device == null) {
            return null;
        }
        ArrayList<URoComponent> components = device.getComponents();
        if (components == null || components.size() == 0) {
            return null;
        }
        ArrayList<Integer> componentIDS = new ArrayList<>();
        for (int i = 0; i < components.size(); i++) {
            if (componentType.equals(components.get(i).getComponentType())) {
                componentIDS.add(components.get(i).getComponentId());
            }
        }
        int[] ids = new int[componentIDS.size()];
        for (int i = 0; i < componentIDS.size(); i++) {
            ids[i] = componentIDS.get(i);
        }
        return ids;
    }

    public static int getSensorValue(URoComponentType type, int id) {
        int invalidValue = SensorValueData.INVALID_VALUE;//无效值
        URoProduct device = UkitDeviceCompact.getInstance().getDefaultDevice();
        if (device == null) {
            return invalidValue;
        }
        URoComponent component = device.getComponent(type, id);
        if (component == null) {
            return invalidValue;
        }
        Integer value = null;
        switch (type) {
            case INFRAREDSENSOR:
                value = ((URoInfraredSensor) component).getSensorValue();
                break;
            case TOUCHSENSOR:
                URoTouchSensorValue touchSensorValue = ((URoTouchSensor) component).getSensorValue();
                if (touchSensorValue != null) {
                    URoTouchSensorValue.URoTouchSensorEvent status = touchSensorValue.getStatus();
                    if (status != null) {
                        value = status.ordinal();
                    }
                }
                break;
            case ULTRASOUNDSENSOR:
                value = ((URoUltrasoundSensor) component).getSensorValue();
                break;
            case COLORSENSOR:
                URoColor colorSensorValue = ((URoColorSensor) component).getSensorValue();
                if (colorSensorValue != null) {
                    value = colorSensorValue.getColor();
                }
                break;
            case ENVIRONMENTSENSOR:
                URoHumitureValue humitureValue = ((URoEnvironmentSensor) component).getSensorValue();
                if (humitureValue != null) {
                    value = humitureValue.getHumidity();
                }
                break;
            case BRIGHTNESSSENSOR:
                value = ((URoBrightnessSensor) component).getSensorValue();
                break;
            case SOUNDSENSOR:
                value = ((URoSoundSensor) component).getSensorValue();
                break;
        }
        return value != null ? value : invalidValue;
    }

    public static URoSensorComponent getSensor(URoComponentType type, int id) {
        URoProduct device = UkitDeviceCompact.getInstance().getDefaultDevice();
        if (device == null) {
            return null;
        }
        return device.getComponent(type, id);
    }

    public static boolean isLowBattery() {
        URoProduct device = UkitDeviceCompact.getInstance().getDefaultDevice();
        if (device == null) {
            return false;
        }
        URoBatteryInfo uRoBatteryInfo = device.getBatteryInfo();
        if (uRoBatteryInfo == null) {
            return false;
        }
        return uRoBatteryInfo.isLowBattery();
    }

    public static boolean isEmptyBattery() {
        URoProduct device = UkitDeviceCompact.getInstance().getDefaultDevice();
        if (device == null) {
            return false;
        }
        URoBatteryInfo uRoBatteryInfo = device.getBatteryInfo();
        if (uRoBatteryInfo == null) {
            return false;
        }
        return uRoBatteryInfo.isEmptyBattery();
    }

    public static boolean isCharging() {
        URoProduct device = UkitDeviceCompact.getInstance().getDefaultDevice();
        if (device == null) {
            return false;
        }
        URoBatteryInfo uRoBatteryInfo = device.getBatteryInfo();
        if (uRoBatteryInfo == null) {
            return false;
        }
        return uRoBatteryInfo.isCharging();
    }

    public static URoBatteryInfo getBatteryInfo() {
        URoProduct device = UkitDeviceCompact.getInstance().getDefaultDevice();
        if (device == null) {
            return null;
        }
        return device.getBatteryInfo();
    }

    public static void updateBoardVersion(String version) {
        URoMainBoardInfo mainBoardInfo = UkitDeviceCompact.getInstance().getMainBoardInfo();
        if (mainBoardInfo != null) {
            mainBoardInfo.boardVersion = version;
        }
    }

    public static void updateSensorVersion(URoComponentType sensorType, String version) {
        URoMainBoardInfo mainBoardInfo = UkitDeviceCompact.getInstance().getMainBoardInfo();
        if (mainBoardInfo != null) {
            URoComponentInfo sensor = mainBoardInfo.getComponentInfo(sensorType);
            if (sensor != null) {
                sensor.updateVersion(version);
            }
        }
    }

    public static void updateSteeringGearVersion(String version) {
        URoMainBoardInfo mainBoardInfo = UkitDeviceCompact.getInstance().getMainBoardInfo();
        if (mainBoardInfo != null) {
            URoComponentInfo steeringGear = mainBoardInfo.getServosInfo();
            if (steeringGear != null) {
                steeringGear.updateVersion(version);
            }
        }
    }

    public static boolean isComponentHasErrorVersion(URoComponentType componentType) {
        URoMainBoardInfo mainBoardInfo = UkitDeviceCompact.getInstance().getMainBoardInfo();
        if (mainBoardInfo != null && componentType != null) {
            URoComponentInfo component = mainBoardInfo.getComponentInfo(componentType);
            if (component != null) {
                HashMap<Integer, String> versions = component.getVersions();
                if (versions.containsValue(URoComponent.INVALID_VERSION)){
                    return true;
                }
                boolean containsErrorVersion = false;
                switch (componentType) {
                    case SERVOS:
                        containsErrorVersion = versions.containsValue("41000000");
                        break;
                    case LED_BELT:
                        containsErrorVersion = versions.containsValue("00000001");
                        break;
                    //todo other sensors
                    default:
                        break;
                }
                return containsErrorVersion;
            }
        }
        return false;
    }

    public static ArrayList<URoComponentID> getPeripheralError() {
        ArrayList<URoComponentID> errors = new ArrayList<>();
        if (UkitDeviceCompact.getInstance().getDefaultDevice() == null) {
            return errors;
        }
        for (URoComponentID componentID : PeripheralErrorCollector.getInstance().getErrorPeripheral()) {
            errors.add(componentID);
        }
        return errors;
    }

    public static boolean isPeripheralError(@NonNull URoComponentType type, int id) {
        for (URoComponentID componentID : PeripheralErrorCollector.getInstance().getErrorPeripheral()) {
            if (componentID.getComponentType().equals(type) && componentID.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public static void resetPeripheralError() {
        if (UkitDeviceCompact.getInstance().getDefaultDevice() == null) {
            return;
        }
        PeripheralErrorCollector.getInstance().clearErrorPeripheral();
    }

    public static void addConnectStatusChangeListener(@NonNull URoConnectStatusChangeListener listener) {
        UkitDeviceCompact.getInstance().addConnectStatusChangeListener(listener);
    }

    public static void removeConnectStatusChangeListener(@NonNull URoConnectStatusChangeListener listener) {
        UkitDeviceCompact.getInstance().removeConnectStatusChangeListener(listener);
    }

    public static void addBatteryChangeListener(@NonNull URoBatteryChangeListener listener) {
        UkitDeviceCompact.getInstance().addBatteryChangeListener(listener);
    }

    public static void removeBatteryChangeListener(@NonNull URoBatteryChangeListener listener) {
        UkitDeviceCompact.getInstance().removeBatteryChangeListener(listener);
    }

    public static void addComponentErrorListener(@NonNull URoComponentErrorListener listener) {
        UkitDeviceCompact.getInstance().addComponentErrorListener(listener);
    }

    public static void removeComponentErrorListener(@NonNull URoComponentErrorListener listener) {
        UkitDeviceCompact.getInstance().removeComponentErrorListener(listener);
    }

    public static void addPushMessageReceivedListener(@NonNull URoPushMessageReceivedListener listener) {
        UkitDeviceCompact.getInstance().addPushMessageReceivedListener(listener);
    }

    public static void removePushMessageReceivedListener(@NonNull URoPushMessageReceivedListener listener) {
        UkitDeviceCompact.getInstance().removePushMessageReceivedListener(listener);
    }

    public static void addNetworkStateListener(@NonNull INetworkStateChangedListener listener) {
        CommonBoardNetworkStateHolder.getInstance().addNetworkStateListener(listener);
    }

    public static void removeNetworkStateListener(@NonNull INetworkStateChangedListener listener) {
        CommonBoardNetworkStateHolder.getInstance().removeNetworkStateListener(listener);
    }

    public static void addWiFiStateInfoChangedListener(@NonNull IWiFiStateInfoChangedListener listener) {
        CommonBoardNetworkStateHolder.getInstance().addWiFiStateInfoChangedListener(listener);
    }

    public static void removeWiFiStateInfoChangedListener(@NonNull IWiFiStateInfoChangedListener listener) {
        CommonBoardNetworkStateHolder.getInstance().removeWiFiStateInfoChangedListener(listener);
    }

    public static boolean isSmartVersion() {
        URoProduct device = UkitDeviceCompact.getInstance().getDefaultDevice();
        if (device == null) {
            return false;
        }
        return !TextUtils.isEmpty(device.getName()) && device.getName().startsWith("uKit2_");
    }
}
