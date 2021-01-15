/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.bluetooth.connect;

import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoConnectStatusChangeListener;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoMissionCallback;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoUpgradeCallback;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocationSequence;
import com.ubtedu.deviceconnect.libs.base.invocation.URoMissionAbortSignal;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentID;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoMainBoardInfo;
import com.ubtedu.deviceconnect.libs.base.product.URoConnectStatus;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.BtInvocationFactory;
import com.ubtedu.ukit.bluetooth.ota.OtaHelper;
import com.ubtedu.ukit.project.Workspace;
import com.ubtedu.ukit.project.vo.ModelInfo;

import java.util.ArrayList;

/**
 * @Author naOKi
 * @Date 2018/11/20
 **/
public class BluetoothConnectPresenter extends BluetoothConnectContracts.Presenter implements URoConnectStatusChangeListener {

    //    private UKitDeviceActionSequence reloadActionSequence = null;
//    private UKitDeviceActionSequence upgradeActionSequence = null;
    private URoMissionAbortSignal reloadAbortSignal = null;
    private URoMissionAbortSignal upgradeAbortSignal = null;
    private boolean isUpgraded = false;
    private boolean isUpgradeSuccess = false;

    public BluetoothConnectPresenter() {
    }

    @Override
    public void disconnect() {
        BluetoothHelper.disconnect();
    }

    @Override
    public void init() {
        BluetoothConnectContracts.UI ui = getView();
        if (ui == null) {
            return;
        }
        ui.updateBoardUpgradeable(isBoardUpgradeable());
        ui.updateBoardInfo(BluetoothHelper.getBoardInfoData());
        if (Workspace.getInstance() != null && Workspace.getInstance().getProject() != null) {
            ui.updateModelInfo(Workspace.getInstance().getProject().modelInfo);
        }
        BluetoothHelper.addConnectStatusChangeListener(this);
    }

    @Override
    public boolean isWiFiConnected() {
        return false;
    }

    @Override
    public boolean isBoardNetworkConnected() {
        return false;
    }

    @Override
    public void release() {
        BluetoothHelper.removeConnectStatusChangeListener(this);
        if (reloadAbortSignal != null) {
            reloadAbortSignal.abort();
            reloadAbortSignal = null;
        }
        if (upgradeAbortSignal != null) {
            upgradeAbortSignal.abort();
            upgradeAbortSignal = null;
        }
    }

    @Override
    public boolean isBoardUpgradeable() {
        return OtaHelper.isBoardUpgradeable();
    }

    @Override
    public void reloadBoardInfo() {
        BluetoothConnectContracts.UI ui = getView();
        if (ui == null) {
            return;
        }
        ui.getUIDelegate().showLoading(false);

        if (reloadAbortSignal != null && !reloadAbortSignal.isAbort()) {
            reloadAbortSignal.abort();
        }
        URoInvocationSequence invocationSequence = new URoInvocationSequence();
        if (isUpgraded) {
            //升级成功以后的首次刷新，需要等待5000ms或以上
            isUpgraded = false;
            invocationSequence.sleep(5000);
        }
        invocationSequence.action(BtInvocationFactory.readMainboardInfo());
        invocationSequence.setCompletionCallback(new URoMissionCallback<URoMainBoardInfo>() {
            @Override
            public void onMissionNextStep(int currentStep, int totalStep) {

            }

            @Override
            public void onMissionBegin() {
                reloadAbortSignal = getAbortSignal();
            }

            @Override
            public void onMissionEnd() {
                reloadAbortSignal = null;
            }

            @Override
            public void onComplete(URoCompletionResult<URoMainBoardInfo> result) {
                BluetoothConnectContracts.UI ui = getView();
                if (ui == null) {
                    return;
                }
                ui.getUIDelegate().hideLoading();
                ui.refreshUI();
            }
        });
        BluetoothHelper.addCommand(invocationSequence);

//        if(reloadActionSequence != null && !reloadActionSequence.hasFinish()) {
//            reloadActionSequence.abort();
//        }
//        reloadActionSequence = UKitDeviceActionSequence.newActionSequence();
//        if(isUpgraded) {
//            //升级成功以后的首次刷新，需要等待5000ms或以上
//            isUpgraded = false;
//            reloadActionSequence.sleep(5000);
//        }
//        reloadActionSequence.add(UKitDeviceActionSequenceFactory.createRefreshBoardInfoActionSequence());
//        reloadActionSequence.callback(new IActionSequenceCallback() {
//            @Override
//            public void onActionSequenceFinished(UKitDeviceActionSequence bas, boolean isSuccess) {
//                BluetoothConnectContracts.UI ui = getView();
//                if(ui == null) {
//                    return;
//                }
//                ui.getUIDelegate().hideLoading();
//                ui.refreshUI();
//            }
//        });
//        reloadActionSequence.execute();
    }

    @Override
    public void upgradeAbort() {
        if (!isUpgrading()) {
            return;
        }
//        BluetoothHelper.addCommand(UKitCmdSteeringGearUpgradeAbort.newInstance());
//        BluetoothHelper.addCommand(UKitCmdBoardUpgradeAbort.newInstance());
//        BluetoothHelper.addCommand(UKitCmdSensorUpgradeAbort.newInstance(UKitPeripheralType.PERIPHERAL_SENSOR_INFRARED));
//        BluetoothHelper.addCommand(UKitCmdSensorUpgradeAbort.newInstance(UKitPeripheralType.PERIPHERAL_SENSOR_SPEAKER));
//        BluetoothHelper.addCommand(UKitCmdSensorUpgradeAbort.newInstance(UKitPeripheralType.PERIPHERAL_SENSOR_LIGHTING));
//        BluetoothHelper.addCommand(UKitCmdSensorUpgradeAbort.newInstance(UKitPeripheralType.PERIPHERAL_SENSOR_TOUCH));
//        upgradeActionSequence.abort();
        if (upgradeAbortSignal != null && !upgradeAbortSignal.isAbort()) {
            upgradeAbortSignal.abort();
        }
    }

    @Override
    public void upgrade() {
        if (isUpgrading()) {
            return;
        }
        URoInvocationSequence invocationSequence = createUpgradeSequence();
//        upgradeActionSequence.sleep(1000);
//        upgradeActionSequence.callback(new IActionSequenceCallback() {
//            @Override
//            public void onActionSequenceFinished(UKitDeviceActionSequence bas, boolean isSuccess) {
//                BluetoothConnectContracts.UI ui = getView();
//                if(ui == null) {
//                    return;
//                }
//                ui.updateAllProcessFinish(isSuccess);
//            }
//        });
        invocationSequence.sleep(1000);
        invocationSequence.setCompletionCallback(new URoMissionCallback() {
            @Override
            public void onMissionNextStep(int currentStep, int totalStep) {
            }

            @Override
            public void onMissionBegin() {
                upgradeAbortSignal = getAbortSignal();
            }

            @Override
            public void onMissionEnd() {
                upgradeAbortSignal = null;
            }

            @Override
            public void onComplete(URoCompletionResult result) {
                isUpgradeSuccess = result.isSuccess();
                BluetoothConnectContracts.UI ui = getView();
                if (ui == null) {
                    return;
                }
                ui.updateAllProcessFinish(result.isSuccess());

            }
        });
        isUpgraded = true;
//        upgradeActionSequence.execute();
        BluetoothHelper.addCommand(invocationSequence);
    }

    final URoComponentType[] mUpgradeSensorType = new URoComponentType[]{
            URoComponentType.MOTOR,
            URoComponentType.INFRAREDSENSOR,
            URoComponentType.ULTRASOUNDSENSOR,
            URoComponentType.LED,
            URoComponentType.TOUCHSENSOR,
            URoComponentType.ENVIRONMENTSENSOR,
            URoComponentType.BRIGHTNESSSENSOR,
            URoComponentType.SOUNDSENSOR,
            URoComponentType.COLORSENSOR,
            URoComponentType.SPEAKER,
            URoComponentType.LED_BELT
    };

    protected URoInvocationSequence createUpgradeSequence() {
        URoMainBoardInfo bia = BluetoothHelper.getBoardInfoData();
        boolean needRefresh = false;
        boolean needDelay = false;
        //        upgradeActionSequence = UKitDeviceActionSequence.newActionSequence();
        URoInvocationSequence invocationSequence = new URoInvocationSequence();

        if (OtaHelper.isBoardUpgradeable()) {
            OtaHelper.OtaVersionInfo otaVersionInfo = OtaHelper.getBoardOtaVersionInfo();
            //不判断是否内置固件版本，满足OtaHelper.isBoardUpgradeable()则进行升级
            //升级主控
//            upgradeActionSequence.upgrade(true, false, null,
//                    otaVersionInfo.getVersion(), otaVersionInfo.getFile(), -1,
//                    new PeripheralUpgradeMsgListener(true, false, null, otaVersionInfo.getVersion()));
            invocationSequence.action(BtInvocationFactory.upgradeMainBoard(otaVersionInfo, new UpgradeCallback(true, false, null, otaVersionInfo.getVersion())));
            needRefresh = true;
        }
        URoComponentInfo steeringGearData = bia.getServosInfo();
        boolean isSteeringGearUpgradeable = OtaHelper.isSteeringGearUpgradeable();
        boolean isSteeringGearConflict = steeringGearData != null && steeringGearData.getAbnormalIds().isEmpty() && !steeringGearData.getAvailableIds().isEmpty() && !steeringGearData.getConflictIds().isEmpty();
        boolean isSteeringGearConflictOrError = isSteeringGearConflict || BluetoothHelper.isComponentHasErrorVersion(URoComponentType.SERVOS);
        if (isSteeringGearUpgradeable || isSteeringGearConflictOrError) {
            OtaHelper.OtaVersionInfo otaVersionInfo = OtaHelper.getSteeringGearOtaVersionInfo();
            if (isSteeringGearConflictOrError || !otaVersionInfo.isInternal()) {
                if (needRefresh) {
//                    upgradeActionSequence.sleep(5000);
//                    upgradeActionSequence.add(UKitDeviceActionSequenceFactory.createRefreshBoardInfoActionSequence());
                    invocationSequence.sleep(5000);
                    invocationSequence.action(BtInvocationFactory.readMainboardInfo());
                    needRefresh = false;
                }
                //升级舵机
//                upgradeActionSequence.upgrade(false, true, null,
//                        otaVersionInfo.getVersion(), otaVersionInfo.getFile(), -1,
//                        new PeripheralUpgradeMsgListener(false, true, null, otaVersionInfo.getVersion()));
                invocationSequence.action(BtInvocationFactory.upgradeComponent(URoComponentType.SERVOS, otaVersionInfo, new UpgradeCallback(false, true, URoComponentType.SERVOS, otaVersionInfo.getVersion())));
                needDelay = true;
            }
        }
        for (URoComponentType sensorType : mUpgradeSensorType) {
            URoComponentInfo shd = bia.getComponentInfo(sensorType);
            boolean isSensorUpgradeable = OtaHelper.isSensorUpgradeable(sensorType);
            boolean isSensorConflict = shd != null && shd.getAbnormalIds().isEmpty() && !shd.getAvailableIds().isEmpty() && !shd.getConflictIds().isEmpty();
            boolean isSensorConflictOrError = isSensorConflict || BluetoothHelper.isComponentHasErrorVersion(sensorType);
            if (isSensorUpgradeable || isSensorConflictOrError) {
                OtaHelper.OtaVersionInfo otaVersionInfo = OtaHelper.getSensorOtaVersion(sensorType);
                if (otaVersionInfo != null && (isSensorConflictOrError || !otaVersionInfo.isInternal())) {
                    if (needRefresh) {
//                        upgradeActionSequence.sleep(5000);
//                        upgradeActionSequence.add(UKitDeviceActionSequenceFactory.createRefreshBoardInfoActionSequence());
                        invocationSequence.sleep(5000);
                        invocationSequence.action(BtInvocationFactory.readMainboardInfo());
                        needRefresh = false;
                    }
                    if (needDelay) {
//                        upgradeActionSequence.sleep(1000);
                        invocationSequence.sleep(1000);
                    }
//                    upgradeActionSequence.upgrade(false, false, sensorType,
//                            otaVersionInfo.getVersion(), otaVersionInfo.getFile(), -1,
//                            new PeripheralUpgradeMsgListener(false, false, sensorType, otaVersionInfo.getVersion()));
                    invocationSequence.action(BtInvocationFactory.upgradeComponent(sensorType, otaVersionInfo, new UpgradeCallback(false, false, sensorType, otaVersionInfo.getVersion())));
                    needDelay = true;
                }
            }
        }
        return invocationSequence;
    }

    @Override
    public void saveConfig() {
        Workspace workspace = Workspace.getInstance();
        if (workspace == null) {
            BluetoothHelper.setVerification(true);
            return;
        }
        if (workspace.getProject() != null) {
            URoMainBoardInfo bia = BluetoothHelper.getBoardInfoData();
            ModelInfo modelInfo = ModelInfo.newInstance(bia);
            if (workspace.getProject().modelInfo != null) {
                ModelInfo newModelInfo = ModelInfo.newInstance(workspace.getProject().modelInfo);
                ModelInfo.mergeModelInfo(modelInfo, newModelInfo);
                if (!newModelInfo.isSameContent(workspace.getProject().modelInfo)) {
                    workspace.saveProjectFile(newModelInfo);
                }
            } else {
                workspace.saveProjectFile(modelInfo);
            }
        }
        BluetoothHelper.setVerification(true);
    }

    @Override
    public void preparePeripheral() {
        URoMainBoardInfo bid = BluetoothHelper.getBoardInfoData();
        if (bid == null) {
            return;
        }
        URoInvocationSequence invocationSequence = new URoInvocationSequence();
        ArrayList<URoComponentID> components = BluetoothHelper.getComponents();
        if (components != null) {
            for (int i = 0; i < components.size(); i++) {
                URoComponentID componentID = components.get(i);
                if (componentID != null) {
                    switch (componentID.getComponentType()) {
                        case INFRAREDSENSOR:
                        case TOUCHSENSOR:
                        case LED:
                        case ULTRASOUNDSENSOR:
                        case COLORSENSOR:
                            invocationSequence.action(BtInvocationFactory.setEnable(componentID, true));
                            break;
                    }

                }
            }
            BluetoothHelper.addCommand(invocationSequence);
        }

//        UKitDeviceActionSequence actionSequence = UKitDeviceActionSequence.newActionSequence();
//        if(bid.infrared.isOK()) {
//            actionSequence.command(new UKitCmdSensorSwitch.Builder().setEnable(true).setIds(bid.infrared.getAvailableIdByte()).setSensorType(UKitPeripheralType.PERIPHERAL_SENSOR_INFRARED.getPeripheralType()).build());
//        }
//        if(bid.touch.isOK()) {
//            actionSequence.command(new UKitCmdSensorSwitch.Builder().setEnable(true).setIds(bid.touch.getAvailableIdByte()).setSensorType(UKitPeripheralType.PERIPHERAL_SENSOR_TOUCH.getPeripheralType()).build());
//        }
//        if(bid.lighting.isOK()) {
//            actionSequence.command(new UKitCmdSensorSwitch.Builder().setEnable(true).setIds(bid.lighting.getAvailableIdByte()).setSensorType(UKitPeripheralType.PERIPHERAL_SENSOR_LIGHTING.getPeripheralType()).build());
//        }
//        if(bid.ultrasound.isOK()) {
//            actionSequence.command(new UKitCmdSensorSwitch.Builder().setEnable(true).setIds(bid.ultrasound.getAvailableIdByte()).setSensorType(UKitPeripheralType.PERIPHERAL_SENSOR_ULTRASOUND.getPeripheralType()).build());
//        }
//        if(bid.speaker.isOK()) {
//            actionSequence.command(new UKitCmdSensorSwitch.Builder().setEnable(true).setIds(bid.speaker.getAvailableIdByte()).setSensorType(UKitPeripheralType.PERIPHERAL_SENSOR_SPEAKER.getPeripheralType()).build());
//        }
//        if(bid.color.isOK()) {
//            actionSequence.command(new UKitCmdSensorSwitch.Builder().setEnable(true).setIds(bid.color.getAvailableIdByte()).setSensorType(UKitPeripheralType.PERIPHERAL_SENSOR_COLOR.getPeripheralType()).build());
//        }
//        actionSequence.execute();
    }

    @Override
    public boolean isUpgrading() {
//        return upgradeActionSequence != null && !upgradeActionSequence.hasFinish();
        return upgradeAbortSignal != null && !upgradeAbortSignal.isAbort();
    }

    //    private class PeripheralUpgradeMsgListener implements IUKitDeviceUpgradeMsgListener {
//        private String version;
//        private boolean isBoard;
//        private boolean isSteeringGear;
//        private UKitPeripheralType sensorType;
//        private long startMs;
//        public PeripheralUpgradeMsgListener(boolean isBoard, boolean isSteeringGear, UKitPeripheralType sensorType, String version) {
//            this.isBoard = isBoard;
//            this.isSteeringGear = isSteeringGear;
//            this.sensorType = sensorType;
//            this.version = version;
//        }
//        @Override
//        public void onPercentChanged(int percent) {
//            if(!isUpgrading()) {
//                return;
//            }
//            BluetoothConnectContracts.UI ui = getView();
//            if(ui == null) {
//                return;
//            }
//            ui.updateUpgradeProcess(isBoard, isSteeringGear, sensorType, percent);
//        }
//        private String getName() {
//            String result = "";
//            try {
//                if (isBoard) {
//                    result = UKitApplication.getInstance().getString(R.string.bluetooth_connect_motherboard);
//                } else if (isSteeringGear) {
//                    result = UKitApplication.getInstance().getString(R.string.bluetooth_peripheral_steering_gear);
//                } else {
//                    switch (sensorType) {
//                    case PERIPHERAL_SENSOR_INFRARED:
//                        result = UKitApplication.getInstance().getString(R.string.bluetooth_peripheral_infrared);
//                        break;
//
//                    case PERIPHERAL_SENSOR_SPEAKER:
//                        result = UKitApplication.getInstance().getString(R.string.bluetooth_peripheral_speaker);
//                        break;
//
//                    case PERIPHERAL_SENSOR_LIGHTING:
//                        result = UKitApplication.getInstance().getString(R.string.bluetooth_peripheral_lighting);
//                        break;
//
//                    case PERIPHERAL_SENSOR_TOUCH:
//                        result = UKitApplication.getInstance().getString(R.string.bluetooth_peripheral_touch);
//                        break;
//                    }
//                }
//            } catch (Exception e) {
//                // ignore
//            }
//            return result;
//        }
//        @Override
//        public void onBeginUpgrade() {
//            if(!isUpgrading()) {
//                return;
//            }
//            BluetoothConnectContracts.UI ui = getView();
//            if(ui == null) {
//                return;
//            }
//            ui.updateUpgradeProcess(isBoard, isSteeringGear, sensorType, 0);
//            startMs = System.currentTimeMillis();
//            LogUtils.e("%s升级开始 ---> 开始时间：%d", getName(), startMs);
//        }
//        @Override
//        public void onEndUpgrade(int code, String msg) {
//            BluetoothConnectContracts.UI ui = getView();
//            if(ui == null) {
//                return;
//            }
//            if(!isUpgrading()) {
//                ui.updateAllProcessFinish(false);
//                return;
//            }
//            LogUtils.e("%s升级结束 ---> 升级耗时：%dms", getName(), System.currentTimeMillis() - startMs);
//            boolean isSuccess = code == 0;
//            if(isSuccess) {
//                if(isBoard) {
//                    BluetoothHelper.updateBoardVersion(version);
//                } else if(isSteeringGear) {
//                    BluetoothHelper.updateSteeringGearVersion(version);
//                } else {
//                    BluetoothHelper.updateSensorVersion(sensorType, version);
//                }
//            }
//            ui.updateUpgradeResult(isBoard, isSteeringGear, sensorType, isSuccess);
//        }
//        @Override
//        public void onBeginTransmission() {}
//        @Override
//        public void onEndTransmission() {}
//        @Override
//        public void onBeginWriteFlash() {}
//        @Override
//        public void onEndWriteFlash() {}
//    }
    protected class UpgradeCallback implements URoUpgradeCallback {
        private String version;
        private boolean isBoard;
        private boolean isSteeringGear;
        private URoComponentType sensorType;
        private long startMs;

        public UpgradeCallback(boolean isBoard, boolean isSteeringGear, URoComponentType sensorType, String version) {
            this.isBoard = isBoard;
            this.isSteeringGear = isSteeringGear;
            this.sensorType = sensorType;
            this.version = version;
        }

        @Override
        public void onProcessPercentChanged(int percent) {
            if (!isUpgrading()) {
                return;
            }
            BluetoothConnectContracts.UI ui = getView();
            if (ui == null) {
                return;
            }
            ui.updateUpgradeProcess(isBoard, isSteeringGear, sensorType, percent);
        }

        @Override
        public void onComplete(URoCompletionResult result) {
            BluetoothConnectContracts.UI ui = getView();
            if (ui == null) {
                return;
            }
            if (!isUpgrading()) {
                ui.updateAllProcessFinish(false);
                return;
            }
//        LogUtils.e("%s升级结束 ---> 升级耗时：%dms", getName(), System.currentTimeMillis() - startMs);
            boolean isSuccess = result.isSuccess();
            if (isSuccess) {
                if (isBoard) {
                    BluetoothHelper.updateBoardVersion(version);
                    onBoardUpgradeSuccess();
                } else if (isSteeringGear) {
                    BluetoothHelper.updateSteeringGearVersion(version);
                } else {
                    BluetoothHelper.updateSensorVersion(sensorType, version);
                }
            }
            ui.updateUpgradeResult(isBoard, isSteeringGear, sensorType, isSuccess);
        }
    }

    @Override
    public void onConnectStatusChanged(URoProduct product, URoConnectStatus connectStatus) {
        BluetoothConnectContracts.UI ui = getView();
        if (ui == null) {
            return;
        }
        if (connectStatus == URoConnectStatus.DISCONNECTED ) {
            if (BluetoothReconnectTask.getInstance().isRebooting()){
                return;
            }
            ui.getUIDelegate().hideLoading();
            if (reloadAbortSignal != null && !reloadAbortSignal.isAbort()) {
                reloadAbortSignal.abort();
            }
            if (!isUpgradeSuccess && isUpgraded) {
                upgradeAbort();
                ui.updateAllProcessFinish(false);
            }
        }
    }

    protected void onBoardUpgradeSuccess() {
    }

}
