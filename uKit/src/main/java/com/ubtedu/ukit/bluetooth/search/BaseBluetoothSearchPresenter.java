package com.ubtedu.ukit.bluetooth.search;

import android.text.TextUtils;

import com.ubtedu.deviceconnect.libs.base.connector.URoDiscoveryCallback;
import com.ubtedu.deviceconnect.libs.ukit.connector.URoUkitBluetoothInfo;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.UkitDeviceCompact;
import com.ubtedu.ukit.menu.settings.Settings;

import java.util.ArrayList;

public class BaseBluetoothSearchPresenter extends BaseBluetoothSearchContracts.Presenter implements URoDiscoveryCallback<URoUkitBluetoothInfo> {

    @Override
    public void startSearchUkitRobot() {
        BaseBluetoothSearchContracts.UI ui = getView();
        if (ui == null) {
            return;
        }
        ui.updateStartSearchUkitRobotView();
        //开始扫描就重置已连接的蓝牙设备的状态
        BluetoothHelper.disconnect();
        //开始蓝牙扫描，等待结果回调
        BluetoothHelper.startScan(this);
    }

    @Override
    public void stopSearchUkitRobot() {
        BaseBluetoothSearchContracts.UI ui = getView();
        if (ui == null) {
            return;
        }
        ui.updateStopSearchUkitRobotView();
        //停止蓝牙扫描
        BluetoothHelper.stopScan();
    }

    @Override
    public void onDeviceFound(URoUkitBluetoothInfo connectItem) {
        BaseBluetoothSearchContracts.UI ui = getView();
        if (ui == null || connectItem == null || connectItem.device == null || connectItem.device.getName() == null) {
            return;
        }
        //按当前选择的设备过滤蓝牙搜索列表
        String name = connectItem.device.getName();
        if ((int) Settings.getTargetDevice() == UkitDeviceCompact.UKIT_SMART_DEVICE && !isUKitName(name)) {
            return;
        }
        if ((int) Settings.getTargetDevice() == UkitDeviceCompact.UKIT_LEGACY_DEVICE && !isJimuName(name)) {
            return;
        }
        //蓝牙扫描回调，找到新设备，通知UI加入该设备
        ui.addNewFindDevice(connectItem);
    }

    private boolean isJimuName(String name) {
        return !TextUtils.isEmpty(name) && (name.toLowerCase().startsWith("jimu_") || TextUtils.equals(name.toLowerCase(), "jimu"));
    }

    private boolean isUKitName(String name) {
        return !TextUtils.isEmpty(name) && name.startsWith("uKit2_");
    }

    @Override
    public void onDiscoveryFinish() {
        BaseBluetoothSearchContracts.UI ui = getView();
        if (ui == null) {
            return;
        }
        //蓝牙扫描回调，终止查找新设备，通知界面更新
        ui.updateStopSearchUkitRobotView();
    }

    @Override
    public void connectUkitRobot(String name, URoUkitBluetoothInfo device) {
    }

    @Override
    public void batchConfigWifi(ArrayList<URoUkitBluetoothInfo> devices, String ssid, String password) {
    }

    @Override
    public String getMobileWifiSSid() {
        return null;
    }

    @Override
    public void release() {
        //释放所有资源
        BluetoothHelper.stopScan();
    }
}
