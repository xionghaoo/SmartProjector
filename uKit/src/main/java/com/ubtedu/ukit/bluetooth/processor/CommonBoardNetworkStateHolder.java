package com.ubtedu.ukit.bluetooth.processor;

import android.util.Log;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.interfaces.URoConnectStatusChangeListener;
import com.ubtedu.deviceconnect.libs.base.invocation.URoInvocationSequence;
import com.ubtedu.deviceconnect.libs.base.product.URoConnectStatus;
import com.ubtedu.deviceconnect.libs.base.product.URoProduct;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoNetworkState;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoWiFiStatusInfo;
import com.ubtedu.deviceconnect.libs.utils.URoListenerGroup;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;
import com.ubtedu.ukit.bluetooth.BtInvocationFactory;
import com.ubtedu.ukit.bluetooth.IUKitCommandResponse;
import com.ubtedu.ukit.bluetooth.interfaces.INetworkStateChangedListener;
import com.ubtedu.ukit.bluetooth.interfaces.IWiFiStateInfoChangedListener;

public class CommonBoardNetworkStateHolder implements URoConnectStatusChangeListener {

    private URoListenerGroup<INetworkStateChangedListener, URoNetworkState> mNetworkStateChangedListeners;
    private URoListenerGroup<IWiFiStateInfoChangedListener, URoWiFiStatusInfo> mWiFiStateInfoChangedListeners;

    private URoWiFiStatusInfo mWifiStateInfo;
    private URoNetworkState mNetworkState;

    private CommonBoardNetworkStateHolder() {
        mNetworkStateChangedListeners = new URoListenerGroup<INetworkStateChangedListener, URoNetworkState>() {
            @Override
            public void notifyListener(INetworkStateChangedListener listener, URoNetworkState data) {
                listener.onNetworkStateChanged(data);
            }
        };
        mWiFiStateInfoChangedListeners = new URoListenerGroup<IWiFiStateInfoChangedListener, URoWiFiStatusInfo>() {
            @Override
            public void notifyListener(IWiFiStateInfoChangedListener listener, URoWiFiStatusInfo data) {
                listener.onWiFiStateInfoChanged(data);
            }
        };
    }

    public static CommonBoardNetworkStateHolder getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final CommonBoardNetworkStateHolder INSTANCE = new CommonBoardNetworkStateHolder();
    }

    @Override
    public void onConnectStatusChanged(URoProduct product, URoConnectStatus connectStatus) {
        if(URoConnectStatus.DISCONNECTED.equals(connectStatus)) {
            //断开连接时清除状态
            mWifiStateInfo = null;
            mNetworkState = null;
        }
    }
    
    public boolean isNetworkConnected() {
        if (!BluetoothHelper.isConnected() || !BluetoothHelper.isSmartVersion()) {
            return false;
        }
        synchronized (CommonBoardNetworkStateHolder.class) {
            if (mWifiStateInfo == null || mWifiStateInfo.getState() != URoWiFiStatusInfo.URoWiFiState.CONNECTED || mNetworkState == null || mNetworkState != URoNetworkState.CONNECTED) {
                return false;
            }
        }
        return true;
    }

    public boolean isWiFiConnected() {
        if (!BluetoothHelper.isConnected() || !BluetoothHelper.isSmartVersion()) {
            return false;
        }
        synchronized (CommonBoardNetworkStateHolder.class) {
            if (mWifiStateInfo == null || mWifiStateInfo.getState() != URoWiFiStatusInfo.URoWiFiState.CONNECTED) {
                return false;
            }
        }
        return true;
    }

    public void updateWifiState(URoWiFiStatusInfo wifiStatusInfo) {
        synchronized (CommonBoardNetworkStateHolder.class) {
            if(wifiStatusInfo != null && !wifiStatusInfo.equals(mWifiStateInfo)) {
                mWifiStateInfo = wifiStatusInfo;
                mWiFiStateInfoChangedListeners.sendNotifyToListener(mWifiStateInfo);
            }
        }
    }

    public void updateNetworkState(URoNetworkState networkState) {
        synchronized (CommonBoardNetworkStateHolder.class) {
            if(networkState != null && !networkState.equals(mNetworkState)) {
                mNetworkState = networkState;
                mNetworkStateChangedListeners.sendNotifyToListener(mNetworkState);
            }
        }
    }
    
    public URoNetworkState getNetworkState() {
        return mNetworkState;
    }
    
    public URoWiFiStatusInfo getWifiStateInfo() {
        return mWifiStateInfo;
    }

    public void addNetworkStateListener(@NonNull INetworkStateChangedListener listener) {
        mNetworkStateChangedListeners.addListener(listener);
    }

    public void removeNetworkStateListener(@NonNull INetworkStateChangedListener listener) {
        mNetworkStateChangedListeners.removeListener(listener);
    }

    public void addWiFiStateInfoChangedListener(@NonNull IWiFiStateInfoChangedListener listener) {
        mWiFiStateInfoChangedListeners.addListener(listener);
    }

    public void removeWiFiStateInfoChangedListener(@NonNull IWiFiStateInfoChangedListener listener) {
        mWiFiStateInfoChangedListeners.removeListener(listener);
    }

    public void requestUpdateStatusAsync() {
        requestUpdateStatusAsync(true, true);
    }

    /**
     * 状态有变化时结果会在 onWiFiStateInfoChanged 和 onNetworkStateChanged 中回调
     */
    public void requestUpdateStatusAsync(boolean wifiStatus, boolean networkStatus) {
        if(!wifiStatus && !networkStatus) {
            return;
        }
        URoInvocationSequence invocationSequence = new URoInvocationSequence();
        if(wifiStatus) {
            invocationSequence.action(BtInvocationFactory.getWiFiState(new IUKitCommandResponse<URoWiFiStatusInfo>() {
                @Override
                protected void onUKitCommandResponse(URoCompletionResult<URoWiFiStatusInfo> result) {
                    URoWiFiStatusInfo wifiStateInfo = result.getData();
                    Log.i("uro--", "wifi  " + wifiStateInfo);
                    updateWifiState(wifiStateInfo);
                }
            }));
        }
        if(wifiStatus && networkStatus) {
            invocationSequence.sleep(200);
        }
        if(networkStatus) {
            invocationSequence.action(BtInvocationFactory.getNetworkState(new IUKitCommandResponse<URoNetworkState>() {
                @Override
                protected void onUKitCommandResponse(URoCompletionResult<URoNetworkState> result) {
                    URoNetworkState networkState = result.getData();
                    Log.i("uro--", "network  " + networkState);
                    updateNetworkState(networkState);
                }
            }));
        }
        BluetoothHelper.addCommand(invocationSequence);
    }
    
}
