package com.ubtedu.ukit.bluetooth.wifi;

import com.ubtedu.alpha1x.core.mvp.BasePresenter;
import com.ubtedu.alpha1x.core.mvp.BaseUI;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoWiFiScanApInfo;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoWiFiStatusInfo;

import java.util.List;

public interface WifiConfigContracts {
    abstract class UI extends BaseUI<WifiConfigContracts.Presenter> {
        public abstract void startSearchWifi();

        public abstract void stopSearchWifi();

        public abstract void updateWifiList(List<URoWiFiScanApInfo> wifiList);

        public abstract void updateWifiStatus(URoWiFiStatusInfo wifiStatus);

        public abstract void showWifiConnectResult(URoWiFiStatusInfo wifiStatus);
    }

    abstract class Presenter extends BasePresenter<WifiConfigContracts.UI> {
        public abstract void init();

        public abstract void searchWifiList();

        public abstract void stopSearchWifi();

        public abstract void connectWifi(URoWiFiScanApInfo wifiInfo, String password);

        public abstract void disconnectWifi(URoWiFiStatusInfo wifiInfo);
    }
}
