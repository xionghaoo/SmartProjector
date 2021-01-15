package com.ubtedu.ukit.bluetooth.wifi;

import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocationSequence;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoWiFiScanApInfo;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoWiFiStatusInfo;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.BtInvocationFactory;
import com.ubtedu.ukit.bluetooth.IUKitCommandResponse;
import com.ubtedu.ukit.bluetooth.UkitInvocation;
import com.ubtedu.ukit.bluetooth.interfaces.IWiFiStateInfoChangedListener;
import com.ubtedu.ukit.bluetooth.processor.CommonBoardNetworkStateHolder;

import java.util.Arrays;

public class WifiConfigPresenter extends WifiConfigContracts.Presenter implements IWiFiStateInfoChangedListener {
    boolean cancelSearch = false;

    private static final long SETWIFI_CONFIG_TIMEOUT = 20000L;

    @Override
    public void init() {
        BluetoothHelper.addWiFiStateInfoChangedListener(this);
        searchWifiList();
        getWifiState();
    }

    private void getWifiState() {
        WifiConfigContracts.UI ui = getView();
        URoWiFiStatusInfo wifiStatusInfo = CommonBoardNetworkStateHolder.getInstance().getWifiStateInfo();
        if (ui != null && wifiStatusInfo != null) {
            ui.updateWifiStatus(wifiStatusInfo);
        }
        // 只刷新WiFi状态，结果会在 onWiFiStateInfoChanged 中回调
        CommonBoardNetworkStateHolder.getInstance().requestUpdateStatusAsync(true, false);
    }

    @Override
    public void searchWifiList() {
        cancelSearch = false;
        getView().startSearchWifi();
        BluetoothHelper.addCommand(BtInvocationFactory.getWiFiList(new IUKitCommandResponse<URoWiFiScanApInfo[]>() {
            @Override
            protected void onUKitCommandResponse(URoCompletionResult<URoWiFiScanApInfo[]> result) {
                WifiConfigContracts.UI ui = getView();
                if (ui == null) {
                    return;
                }
                if (cancelSearch) {
                    return;
                }
                if (result.isSuccess()) {
                    ui.updateWifiList(Arrays.asList(result.getData()));
                } else {
                    ui.stopSearchWifi();
                }
            }
        }));
    }

    @Override
    public void stopSearchWifi() {
        WifiConfigContracts.UI ui = getView();
        if (ui == null) return;
        ui.stopSearchWifi();
        cancelSearch = true;
    }

    @Override
    public void onWiFiStateInfoChanged(URoWiFiStatusInfo wifiStatusInfo) {
        WifiConfigContracts.UI ui = getView();
        if (ui == null) return;
        if (wifiStatusInfo.getState() == URoWiFiStatusInfo.URoWiFiState.CONNECTED) {
            ui.updateWifiStatus(wifiStatusInfo);
        } else if (wifiStatusInfo.getState() == URoWiFiStatusInfo.URoWiFiState.DISCONNECTED) {
            ui.updateWifiStatus(null);
        }
    }

    @Override
    public void connectWifi(URoWiFiScanApInfo wifiInfo, String password) {
        UkitInvocation invocation = BtInvocationFactory.setWiFiConfig(wifiInfo, password, new IUKitCommandResponse<URoWiFiStatusInfo>() {
            @Override
            protected void onUKitCommandResponse(URoCompletionResult<URoWiFiStatusInfo> result) {
                WifiConfigContracts.UI ui = getView();
                if (ui == null) return;
                ui.getUIDelegate().hideLoading();
                if (result.isSuccess()) {
                    ui.showWifiConnectResult(result.getData());
                    //配网成功，请求刷新网络状态，必须刷，不然没有网络状态和WiFi状态直接运行Ai积木块有可能会提示无网
                    CommonBoardNetworkStateHolder.getInstance().updateWifiState(result.getData());
                    CommonBoardNetworkStateHolder.getInstance().requestUpdateStatusAsync(false, true);
                } else {
                    ui.showWifiConnectResult(null);
                }
            }
        });
        invocation.setTimeoutThreshold(SETWIFI_CONFIG_TIMEOUT);
        BluetoothHelper.addCommand(invocation);
    }

    @Override
    public void disconnectWifi(URoWiFiStatusInfo wifiInfo) {
        BluetoothHelper.addCommand(BtInvocationFactory.disconnectWiFi(new IUKitCommandResponse<Void>() {
            @Override
            protected void onUKitCommandResponse(URoCompletionResult<Void> result) {
                WifiConfigContracts.UI ui = getView();
                if (ui == null) return;
                if (result.isSuccess()) {
                    ui.updateWifiStatus(null);
                }
            }
        }));
    }

    @Override
    public void release() {
        super.release();
        BluetoothHelper.removeWiFiStateInfoChangedListener(this);
    }
}
