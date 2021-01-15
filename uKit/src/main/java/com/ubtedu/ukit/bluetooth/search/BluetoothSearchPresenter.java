/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.bluetooth.search;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.connector.URoDiscoveryCallback;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoConnectStatusChangeListener;
import com.ubtedu.deviceconnect.libs.base.product.URoConnectStatus;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.deviceconnect.libs.ukit.connector.URoUkitBluetoothInfo;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.ota.OtaHelper;

/**
 * @Author naOKi
 * @Date 2018/11/19
 **/
public class BluetoothSearchPresenter extends BaseBluetoothSearchPresenter implements URoDiscoveryCallback<URoUkitBluetoothInfo>, URoConnectStatusChangeListener {

    private boolean mIgnoreDisconnectResult = false;

    public BluetoothSearchPresenter() {
    }

    @Override
    public void startSearchUkitRobot() {
        super.startSearchUkitRobot();
        BluetoothHelper.addConnectStatusChangeListener(this);
    }

    @Override
    public void connectUkitRobot(final String name, URoUkitBluetoothInfo device) {
        BaseBluetoothSearchContracts.UI ui = getView();
        if (ui == null) {
            return;
        }
        BluetoothHelper.disconnect();
        BluetoothHelper.stopScan();
        ui.updateStopSearchUkitRobotView();
        ui.updateStartConnectUkitRobotView(name);
        BluetoothHelper.connect(device, new URoCompletionCallback<Void>() {
            @Override
            public void onComplete(URoCompletionResult<Void> result) {
                BaseBluetoothSearchContracts.UI ui = getView();
                if (ui == null) {
                    return;
                }
                if (!result.isSuccess()) {
                    getView().updateConnectResult(false);
                }

            }
        });
    }

    @Override
    public void release() {
        super.release();
        BluetoothHelper.removeConnectStatusChangeListener(this);
    }

    @Override
    public void onConnectStatusChanged(URoProduct product, URoConnectStatus connectStatus) {
        BaseBluetoothSearchContracts.UI ui = getView();
        if (ui == null) {
            return;
        }
        if (connectStatus == URoConnectStatus.CONNECTED) {
            mIgnoreDisconnectResult = BluetoothHelper.isEmptyBattery() || OtaHelper.isBoardVersionInfoError() || OtaHelper.isDeprecatedBoardVersion();
            ui.updateConnectResult(true);
        } else if (connectStatus == URoConnectStatus.DISCONNECTED) {
            ui.getUIDelegate().hideLoading();
            if (mIgnoreDisconnectResult) {
                mIgnoreDisconnectResult = false;
                return;
            }
            ui.updateConnectResult(false);
        }
    }
}
