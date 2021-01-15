package com.ubtedu.ukit.bluetooth.processor;

import com.ubtedu.bridge.BridgeBoolean;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoBatteryChangeListener;
import com.ubtedu.deviceconnect.libs.base.model.URoBatteryInfo;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.error.BluetoothCommonErrorHelper;
import com.ubtedu.ukit.menu.settings.Settings;
import com.ubtedu.ukit.project.bridge.BridgeCommunicator;
import com.ubtedu.ukit.project.bridge.MotionDesigner;
import com.ubtedu.ukit.project.bridge.functions.BlocklyFunctions;
import com.ubtedu.ukit.project.bridge.functions.Unity3DFunctions;
import com.ubtedu.ukit.project.controller.ControllerModeHolder;

/**
 * @Author naOKi
 * @Date 2018/11/08
 **/
public class CommonBatteryEventHandler implements URoBatteryChangeListener {

    private CommonBatteryEventHandler() {
    }

    private static CommonBatteryEventHandler instance = null;
    private URoBatteryInfo oldBatteryInfoData;

    public static CommonBatteryEventHandler getInstance() {
        synchronized (CommonBatteryEventHandler.class) {
            if (instance == null) {
                instance = new CommonBatteryEventHandler();
            }
            return instance;
        }
    }
//
//    @Override
//    public void onBatteryStateChanged(UKitRemoteDevice device, URoUkitBatteryInfo oldBatteryInfoData, URoUkitBatteryInfo newBatteryInfoData) {
//        if (BluetoothHelper.isConnected()) {
//            if ((oldBatteryInfoData == null || !oldBatteryInfoData.isEmptyBattery()) && newBatteryInfoData.isEmptyBattery()) {
//                //空电量，断开连接
//                BluetoothCommonErrorHelper.openBluetoothCommonErrorActivity(BluetoothCommonErrorHelper.CommonError.LOW_POWER);
//                if(!PeripheralErrorCollector.getInstance().isCurrentType(PeripheralErrorCollector.ErrorCollectorType.NONE)) {
//                    BluetoothHelper.terminateExecution();
//                }
//                BluetoothHelper.disconnect();
//            } else if ((oldBatteryInfoData == null || !oldBatteryInfoData.isLowBattery()) && newBatteryInfoData.isLowBattery()) {
//                //低电量，停止运行
//                BluetoothCommonErrorHelper.openBluetoothCommonErrorActivity(BluetoothCommonErrorHelper.CommonError.LOW_POWER);
//                if(!PeripheralErrorCollector.getInstance().isCurrentType(PeripheralErrorCollector.ErrorCollectorType.NONE)) {
//                    BluetoothHelper.terminateExecution();
//                }
//            }
//            if (PeripheralErrorCollector.getInstance().getCollectorState() == PeripheralErrorCollector.CollectorState.START
//                    && (oldBatteryInfoData == null || !oldBatteryInfoData.isCharging()) && Settings.isChargingProtection() && newBatteryInfoData.isCharging()) {
//                //充电保护
//                BluetoothHelper.terminateExecution();
//                BluetoothHelper.getBtHandler().post(new Runnable() {
//                    @Override
//                    public void run() {
//                        ControllerModeHolder.setControllerMode(ControllerModeHolder.MODE_EDIT);
//                    }
//                });
//                BluetoothCommonErrorHelper.openBluetoothCommonErrorActivity(BluetoothCommonErrorHelper.CommonError.CHARGING_PROTECTION);
//            }
//        }
//        if (oldBatteryInfoData == null || oldBatteryInfoData.isCharging() != newBatteryInfoData.isCharging()) {
//            //通知Blockly&Unity充电状态发生变化
//            Object[] args = new Object[]{BridgeBoolean.wrap(newBatteryInfoData.isCharging()), BridgeBoolean.wrap(Settings.isChargingProtection())};
//            if (BridgeCommunicator.getInstance().getBlocklyBridge(false).isCommunicable()) {
//                BridgeCommunicator.getInstance().getBlocklyBridge(false).call(BlocklyFunctions.notifyChargeStateChange, args, null);
//            }
//            if (BridgeCommunicator.getInstance().getBlocklyBridge(true).isCommunicable()) {
//                BridgeCommunicator.getInstance().getBlocklyBridge(true).call(BlocklyFunctions.notifyChargeStateChange, args, null);
//            }
//            if (BridgeCommunicator.getInstance().getUnity3DBridge().isCommunicable()) {
//                BridgeCommunicator.getInstance().getUnity3DBridge().call(Unity3DFunctions.notifyChargeStateChange, args, null);
//            }
//        }
//        MotionDesigner.getInstance().updateChargingFlag(newBatteryInfoData.isCharging());
//    }

    @Override
    public void onBatteryInfoUpdated(URoProduct product, URoBatteryInfo newBatteryInfoData) {
        if (BluetoothHelper.isConnected()) {
            if ((oldBatteryInfoData == null || !oldBatteryInfoData.isEmptyBattery()) && newBatteryInfoData.isEmptyBattery()) {
                //空电量，断开连接
                BluetoothCommonErrorHelper.openBluetoothCommonErrorActivity(BluetoothCommonErrorHelper.CommonError.EMPTY_POWER);
                if (!PeripheralErrorCollector.getInstance().isCurrentType(PeripheralErrorCollector.ErrorCollectorType.NONE)) {
                    BluetoothHelper.terminateExecution();
                }
                BluetoothHelper.disconnect();
            } else if ((oldBatteryInfoData == null || !oldBatteryInfoData.isLowBattery()) && newBatteryInfoData.isLowBattery()) {
                //低电量，停止运行
                BluetoothCommonErrorHelper.openBluetoothCommonErrorActivity(BluetoothCommonErrorHelper.CommonError.LOW_POWER);
                if (!PeripheralErrorCollector.getInstance().isCurrentType(PeripheralErrorCollector.ErrorCollectorType.NONE)) {
                    BluetoothHelper.terminateExecution();
                }
            }
            if (PeripheralErrorCollector.getInstance().getCollectorState() == PeripheralErrorCollector.CollectorState.START
                    && (oldBatteryInfoData == null || !oldBatteryInfoData.isCharging()) && Settings.isChargingProtection() && newBatteryInfoData.isCharging()) {
                //充电保护
                BluetoothHelper.terminateExecution();
                BluetoothHelper.getBtHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        ControllerModeHolder.setControllerMode(ControllerModeHolder.MODE_EDIT);
                    }
                });
                BluetoothCommonErrorHelper.openBluetoothCommonErrorActivity(BluetoothCommonErrorHelper.CommonError.CHARGING_PROTECTION);
            }
        }
        if (oldBatteryInfoData == null || oldBatteryInfoData.isCharging() != newBatteryInfoData.isCharging()) {
            //通知Blockly&Unity充电状态发生变化
            Object[] args = new Object[]{BridgeBoolean.wrap(newBatteryInfoData.isCharging()), BridgeBoolean.wrap(Settings.isChargingProtection())};
            if (BridgeCommunicator.getInstance().getBlocklyBridge(false).isCommunicable()) {
                BridgeCommunicator.getInstance().getBlocklyBridge(false).call(BlocklyFunctions.notifyChargeStateChange, args, null);
            }
            if (BridgeCommunicator.getInstance().getBlocklyBridge(true).isCommunicable()) {
                BridgeCommunicator.getInstance().getBlocklyBridge(true).call(BlocklyFunctions.notifyChargeStateChange, args, null);
            }
            if (BridgeCommunicator.getInstance().getUnity3DBridge().isCommunicable()) {
                BridgeCommunicator.getInstance().getUnity3DBridge().call(Unity3DFunctions.notifyChargeStateChange, args, null);
            }
        }
        MotionDesigner.getInstance().updateChargingFlag(newBatteryInfoData.isCharging());
        oldBatteryInfoData = newBatteryInfoData;
    }
}
