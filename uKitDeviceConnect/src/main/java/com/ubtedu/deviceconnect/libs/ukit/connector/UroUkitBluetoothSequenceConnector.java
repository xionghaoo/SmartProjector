package com.ubtedu.deviceconnect.libs.ukit.connector;

import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocation;
import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationNames;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoWiFiAuthMode;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoWiFiScanApInfo;

import java.util.ArrayList;

public class UroUkitBluetoothSequenceConnector<T> {

    private static class Holder {
        private static final UroUkitBluetoothSequenceConnector mInstance = new UroUkitBluetoothSequenceConnector();
    }

    public static UroUkitBluetoothSequenceConnector getInstance() {
        return Holder.mInstance;
    }

    private UroUkitBluetoothSequenceConnector() {
    }


    public void connectDevicesWithInitInvocation(ArrayList<URoUkitBluetoothInfo> devices, long timeout, URoInvocation invocation, UroSequenceConnectMissionCallback callback) {
        UroUkitBluetoothSequenceConnectMission mission = new UroUkitBluetoothSequenceConnectMission(devices, timeout, invocation, callback);
        mission.start();
    }

    public void connectDevicesWithInitWifi(ArrayList<URoUkitBluetoothInfo> devices, String wifiSsid, String password, UroSequenceConnectMissionCallback callback) {
        URoInvocation invocation = new URoInvocation(URoInvocationNames.INVOCATION_SET_WIFI_CONFIG);
        invocation.setParameter("scanApInfo", new URoWiFiScanApInfo(wifiSsid, 0, URoWiFiAuthMode.NONE));
        invocation.setParameter("password", password);
        connectDevicesWithInitInvocation(devices, 15000, invocation, callback);
    }
}
