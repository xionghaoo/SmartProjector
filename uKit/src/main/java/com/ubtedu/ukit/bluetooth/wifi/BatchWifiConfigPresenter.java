/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.bluetooth.wifi;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.connector.URoDiscoveryCallback;
import com.ubtedu.deviceconnect.libs.base.invocation.URoMissionAbortSignal;
import com.ubtedu.deviceconnect.libs.ukit.connector.URoUkitBluetoothInfo;
import com.ubtedu.deviceconnect.libs.ukit.connector.UroSequenceConnectMissionCallback;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoWiFiStatusInfo;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.processor.CommonConnectEventHandler;
import com.ubtedu.ukit.bluetooth.search.BaseBluetoothSearchContracts;
import com.ubtedu.ukit.bluetooth.search.BaseBluetoothSearchPresenter;

import java.util.ArrayList;

import static android.content.Context.WIFI_SERVICE;

/**
 * @Author naOKi
 * @Date 2018/11/19
 **/
public class BatchWifiConfigPresenter extends BaseBluetoothSearchPresenter implements URoDiscoveryCallback<URoUkitBluetoothInfo> {
    private URoMissionAbortSignal mWifiConfigSignal;

    public BatchWifiConfigPresenter() {
    }

    @Override
    public void release() {
        CommonConnectEventHandler.getInstance().setToastOpen(true);
        super.release();
    }

    @Override
    public void batchConfigWifi(final ArrayList<URoUkitBluetoothInfo> devices,String ssid, String password) {
        if (TextUtils.isEmpty(ssid)) {
            BaseBluetoothSearchContracts.UI ui = getView();
            if (ui == null) {
                return;
            }
            ui.getUIDelegate().toastShort(mContext.getString(R.string.batch_config_wifi_mobile_wifi_none_msg));
            return;
        }

        CommonConnectEventHandler.getInstance().setToastOpen(false);
        BluetoothHelper.connectDevicesWithWifi(devices, ssid, password, new UroSequenceConnectMissionCallback<URoWiFiStatusInfo>() {
            @Override
            public void onMissionNextStep(int currentStep, int totalStep) {
            }

            @Override
            public void onProcessPercentChanged(int percent) {
                BaseBluetoothSearchContracts.UI ui = getView();
                if (ui == null) {
                    return;
                }
                ui.updateConfigProgress(percent);
            }

            @Override
            public void onMissionBegin() {
                mWifiConfigSignal = getAbortSignal();
                BaseBluetoothSearchContracts.UI ui = getView();
                if (ui == null) {
                    return;
                }
                ui.startConfigWifi();
            }

            @Override
            public void onMissionEnd() {
            }

            @Override
            public void onComplete(URoCompletionResult<ArrayList<URoCompletionResult<URoWiFiStatusInfo>>> result) {
                BaseBluetoothSearchContracts.UI ui = getView();
                if (ui == null) {
                    return;
                }
                ui.getUIDelegate().toastShort(mContext.getString(R.string.batch_config_wifi_complete_msg));
                if (result.isSuccess()) {
                    ArrayList<URoCompletionResult<URoWiFiStatusInfo>> uRoCompletionResults = result.getData();
                    ArrayList<Boolean> configResultList = new ArrayList<>();
                    for (int i = 0; i < uRoCompletionResults.size(); i++) {
                        URoCompletionResult<URoWiFiStatusInfo> wifiResult = uRoCompletionResults.get(i);
                        configResultList.add(wifiResult != null && wifiResult.isSuccess() && URoWiFiStatusInfo.URoWiFiState.CONNECTED == wifiResult.getData().getState());
                    }
                    ui.updateConfigResult(configResultList);
                }
            }

            @Override
            public void onStepResponse(int step, URoCompletionResult<URoWiFiStatusInfo> result) {
                if (result != null && result.getData() != null && result.getData().getDisconnectReason() == URoWiFiStatusInfo.URoWiFiDisconnectReason.REASON_4WAY_HANDSHAKE_TIMEOUT) {
                    if (mWifiConfigSignal != null) {
                        mWifiConfigSignal.abort();
                    }
                    BaseBluetoothSearchContracts.UI ui = getView();
                    if (ui == null) {
                        return;
                    }
                    ArrayList<Boolean> configResultList = new ArrayList<>();
                    for (int i = 0; i < devices.size(); i++) {
                        configResultList.add(false);
                    }
                    ui.updateConfigResult(configResultList);
                    ui.onWifiPasswordWrong();
                }
            }
        });
    }

    @Override
    public String getMobileWifiSSid() {
        WifiManager wm = (WifiManager) mContext.getSystemService(WIFI_SERVICE);
        if (wm != null) {
            WifiInfo winfo = wm.getConnectionInfo();
            if (winfo != null) {
                String s = winfo.getSSID();
                if (s.length() > 2 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
                    return s.substring(1, s.length() - 1);
                }
            }
        }
        return "";
    }
}
