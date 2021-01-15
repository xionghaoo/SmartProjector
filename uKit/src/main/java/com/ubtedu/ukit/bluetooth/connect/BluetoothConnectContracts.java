package com.ubtedu.ukit.bluetooth.connect;


import com.ubtedu.alpha1x.core.mvp.BasePresenter;
import com.ubtedu.alpha1x.core.mvp.BaseUI;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.model.URoMainBoardInfo;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoNetworkState;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoWiFiStatusInfo;
import com.ubtedu.ukit.project.vo.ModelInfo;

/**
 * @Author naOKi
 * @Date 2018/11/20
 **/
public interface BluetoothConnectContracts {
    abstract class UI extends BaseUI<Presenter> {
        public abstract void updateBoardInfo(URoMainBoardInfo data);
        public abstract void updateModelInfo(ModelInfo data);
        public abstract void updateBoardUpgradeable(boolean upgradeable);
        public abstract void updateUpgradeProcess(boolean isBoard, boolean isSteeringGear, URoComponentType sensorType, int process);
        public abstract void updateUpgradeResult(boolean isBoard, boolean isSteeringGear, URoComponentType sensorType, boolean isSuccess);
        public abstract void updateAllProcessFinish(boolean isSuccess);
        public abstract void updateWifiInfo(URoWiFiStatusInfo wifiInfo, URoNetworkState networkState);
        public abstract void refreshUI();
        public abstract void restartBoardAfterUpgrade();
    }

    abstract class Presenter extends BasePresenter<UI> {
        public abstract void disconnect();
        public abstract void init();
        public abstract boolean isBoardUpgradeable();
        public abstract void reloadBoardInfo();
        public abstract boolean isUpgrading();
        public abstract void upgrade();
        public abstract void upgradeAbort();
        public abstract void saveConfig();
        public abstract void preparePeripheral();
        public abstract boolean isBoardNetworkConnected();
        public abstract boolean isWiFiConnected();
    }
}
