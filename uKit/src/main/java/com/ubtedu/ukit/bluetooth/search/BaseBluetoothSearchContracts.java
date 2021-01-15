package com.ubtedu.ukit.bluetooth.search;


import com.ubtedu.alpha1x.core.mvp.BasePresenter;
import com.ubtedu.alpha1x.core.mvp.BaseUI;
import com.ubtedu.deviceconnect.libs.ukit.connector.URoUkitBluetoothInfo;

import java.util.ArrayList;

/**
 * @Author naOKi
 * @Date 2018/11/19
 **/
public interface BaseBluetoothSearchContracts {
    abstract class UI extends BaseUI<Presenter> {
        public abstract void updateStartSearchUkitRobotView();

        public abstract void updateStopSearchUkitRobotView();

        public abstract void addNewFindDevice(URoUkitBluetoothInfo device);

        public abstract void updateStartConnectUkitRobotView(String name);

        public abstract void updateConnectResult(boolean isSuccess);

        public abstract void startConfigWifi();

        public abstract void updateConfigProgress(int progress);

        public abstract void updateConfigResult(ArrayList<Boolean> configResultList);

        public abstract void onWifiPasswordWrong();
    }

    abstract class Presenter extends BasePresenter<UI> {
        public abstract void startSearchUkitRobot();

        public abstract void stopSearchUkitRobot();

        public abstract void connectUkitRobot(String name, URoUkitBluetoothInfo device);

        public abstract void batchConfigWifi(ArrayList<URoUkitBluetoothInfo> devices, String ssid, String password);

        public abstract String getMobileWifiSSid();
    }
}
