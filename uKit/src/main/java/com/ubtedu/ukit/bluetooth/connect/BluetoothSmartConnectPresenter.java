package com.ubtedu.ukit.bluetooth.connect;

import android.util.Log;

import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocationSequence;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentInfo;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoMainBoardInfo;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoNetworkState;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoWiFiStatusInfo;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.BtInvocationFactory;
import com.ubtedu.ukit.bluetooth.IUKitCommandResponse;
import com.ubtedu.ukit.bluetooth.interfaces.INetworkStateChangedListener;
import com.ubtedu.ukit.bluetooth.interfaces.IWiFiStateInfoChangedListener;
import com.ubtedu.ukit.bluetooth.ota.OtaHelper;
import com.ubtedu.ukit.bluetooth.processor.CommonBoardNetworkStateHolder;

public class BluetoothSmartConnectPresenter extends BluetoothConnectPresenter implements IWiFiStateInfoChangedListener, INetworkStateChangedListener {
    @Override
    public void init() {
        super.init();
        BluetoothHelper.addWiFiStateInfoChangedListener(this);
        BluetoothHelper.addNetworkStateListener(this);
        BluetoothHelper.addConnectStatusChangeListener(this);
        initWifiInfoWithCacheStatus();
    }

    @Override
    public void release() {
        super.release();
        BluetoothHelper.removeWiFiStateInfoChangedListener(this);
        BluetoothHelper.removeNetworkStateListener(this);
        BluetoothHelper.removeConnectStatusChangeListener(this);
    }

    @Override
    public void onResume() {
        getWifiInfo();
    }

    private URoWiFiStatusInfo mWifiStateInfo;
    private URoNetworkState mNetworkState;

    @Override
    public void onNetworkStateChanged(URoNetworkState networkState) {
        Log.i("uro--", "network  changed==" + networkState.toString());
        mNetworkState = networkState;
        updateWifiInfo();
    }

    @Override
    public void onWiFiStateInfoChanged(URoWiFiStatusInfo wifiStatusInfo) {
        Log.i("uro--", "wifi  changed==" + wifiStatusInfo.toString());
        mWifiStateInfo = wifiStatusInfo;
        updateWifiInfo();
    }

    @Override
    public boolean isWiFiConnected() {
        if (mWifiStateInfo != null && mWifiStateInfo.getState() == URoWiFiStatusInfo.URoWiFiState.CONNECTED) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isBoardNetworkConnected() {
        if (mWifiStateInfo == null || mWifiStateInfo.getState() != URoWiFiStatusInfo.URoWiFiState.CONNECTED) {
            return false;
        }
        return mNetworkState != null && mNetworkState == URoNetworkState.CONNECTED;
    }

    private void getWifiInfo() {
        if(mWifiStateInfo != null && mNetworkState != null) {
            //update with last status
            updateWifiInfo();
        }
        // 结果会在onNetworkStateChanged和onWiFiStateInfoChanged中回调
        CommonBoardNetworkStateHolder.getInstance().requestUpdateStatusAsync();
    }
    
    private void initWifiInfoWithCacheStatus() {
        mWifiStateInfo = CommonBoardNetworkStateHolder.getInstance().getWifiStateInfo();
        mNetworkState = CommonBoardNetworkStateHolder.getInstance().getNetworkState();
    }

    private void updateWifiInfo() {
        BluetoothConnectContracts.UI ui = getView();
        if (ui == null) {
            return;
        }
        ui.updateWifiInfo(mWifiStateInfo, mNetworkState);
    }

    @Override
    protected URoInvocationSequence createUpgradeSequence() {
        URoInvocationSequence invocationSequence = new URoInvocationSequence();
        boolean needDelay = false;
        URoMainBoardInfo bia = BluetoothHelper.getBoardInfoData();
        URoComponentInfo steeringGearData = bia.getServosInfo();
        boolean isSteeringGearUpgradeable = OtaHelper.isSteeringGearUpgradeable();
        boolean isSteeringGearConflict = steeringGearData != null && steeringGearData.getAbnormalIds().isEmpty() && !steeringGearData.getAvailableIds().isEmpty() && !steeringGearData.getConflictIds().isEmpty();
        boolean isSteeringGearConflictOrError = isSteeringGearConflict || BluetoothHelper.isComponentHasErrorVersion(URoComponentType.SERVOS);
        if (isSteeringGearUpgradeable || isSteeringGearConflictOrError) {
            OtaHelper.OtaVersionInfo otaVersionInfo = OtaHelper.getSteeringGearOtaVersionInfo();
            if (isSteeringGearConflictOrError || !otaVersionInfo.isInternal()) {
                invocationSequence.action(BtInvocationFactory.stopExecScript(null));
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
                    if (needDelay) {
                        invocationSequence.sleep(1000);
                    }
                    invocationSequence.action(BtInvocationFactory.stopExecScript(null));
                    invocationSequence.action(BtInvocationFactory.upgradeComponent(sensorType, otaVersionInfo, new UpgradeCallback(false, false, sensorType, otaVersionInfo.getVersion())));
                    needDelay = true;
                }
            }
        }
        if (OtaHelper.isBoardUpgradeable()) {
            OtaHelper.OtaVersionInfo otaVersionInfo = OtaHelper.getBoardOtaVersionInfo();
            //不判断是否内置固件版本，满足OtaHelper.isBoardUpgradeable()则进行升级
            //升级主控
            invocationSequence.action(BtInvocationFactory.stopExecScript(null));
            invocationSequence.action(BtInvocationFactory.upgradeMainBoard(otaVersionInfo, new UpgradeCallback(true, false, null, otaVersionInfo.getVersion())));
        }
        return invocationSequence;
    }

    @Override
    protected void onBoardUpgradeSuccess() {
        super.onBoardUpgradeSuccess();
        restartBoard();
    }

    private void restartBoard() {
        BluetoothReconnectTask.getInstance().reboot(mBluetoothReconnectCallback);
    }

    private BluetoothReconnectTask.IReconnectCallback mBluetoothReconnectCallback = new BluetoothReconnectTask.IReconnectCallback() {
        @Override
        public void onStartReconnect() {
            BluetoothConnectContracts.UI ui = getView();
            if (ui == null) {
                return;
            }
            ui.restartBoardAfterUpgrade();
        }

        @Override
        public void onReconnectComplete(boolean isSuccess) {
            BluetoothConnectContracts.UI ui = getView();
            if (ui == null) {
                return;
            }
            ui.getUIDelegate().hideLoading();
            ui.refreshUI();
            if (isSuccess) {
                getWifiInfo();
            }
        }
    };
}
