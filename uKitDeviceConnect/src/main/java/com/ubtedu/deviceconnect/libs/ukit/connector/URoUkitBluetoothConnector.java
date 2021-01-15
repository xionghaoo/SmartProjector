package com.ubtedu.deviceconnect.libs.ukit.connector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.URoCompletionCallbackHelper;
import com.ubtedu.deviceconnect.libs.base.URoCompletionCallbackWrap;
import com.ubtedu.deviceconnect.libs.base.URoCompletionResult;
import com.ubtedu.deviceconnect.libs.base.URoSDK;
import com.ubtedu.deviceconnect.libs.base.connector.URoBluetoothDevice;
import com.ubtedu.deviceconnect.libs.base.connector.URoConnectMission;
import com.ubtedu.deviceconnect.libs.base.connector.URoConnector;
import com.ubtedu.deviceconnect.libs.base.connector.URoDiscoveryCallback;
import com.ubtedu.deviceconnect.libs.base.link.URoLinkType;
import com.ubtedu.deviceconnect.libs.base.model.URoError;
import com.ubtedu.deviceconnect.libs.base.product.UroProductInitDelegate;
import com.ubtedu.deviceconnect.libs.base.router.URoLinkRouter;
import com.ubtedu.deviceconnect.libs.ukit.legacy.connector.URoUkitLegacyConnectMission;
import com.ubtedu.deviceconnect.libs.ukit.legacy.link.URoUkitLegacyStreamLink;
import com.ubtedu.deviceconnect.libs.ukit.legacy.model.URoUkitLegacyBtLinkModel;
import com.ubtedu.deviceconnect.libs.ukit.smart.connector.URoUkitSmartBleMgrDelegate;
import com.ubtedu.deviceconnect.libs.ukit.smart.connector.URoUkitSmartBleConnectMission;
import com.ubtedu.deviceconnect.libs.ukit.smart.link.URoUkitSmartBleLink;
import com.ubtedu.deviceconnect.libs.ukit.smart.model.URoUkitSmartBtLinkModel;
import com.ubtedu.deviceconnect.libs.utils.URoIoUtils;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

/**
 * @Author naOKi
 * @Date 2019/08/12
 **/
public class URoUkitBluetoothConnector extends URoConnector<URoUkitBluetoothInfo> {

    private BluetoothAdapter adapter;
    private Handler handler;

    private final Object discoveryLock = new Object();

    private URoDiscoveryCallback<URoUkitBluetoothInfo> callback;
    private BroadcastReceiver discoveryReceiver;
    private Runnable discoveryTimeoutRunnable;

    private URoConnectMission connectMission;

    static final class SingleHolder {
        static URoUkitBluetoothConnector INSTANCE = null;
    }

    public static URoUkitBluetoothConnector getInstance() {
        synchronized (SingleHolder.class) {
            if(SingleHolder.INSTANCE == null) {
                SingleHolder.INSTANCE = new URoUkitBluetoothConnector();
            }
            return SingleHolder.INSTANCE;
        }
    }

    public URoUkitBluetoothConnector() {
        this.adapter = BluetoothAdapter.getDefaultAdapter();
        this.handler = new Handler(Looper.getMainLooper());
        this.discoveryTimeoutRunnable = () -> {
            discoveryFinish();
        };
    }

    @Override
    public boolean isSearching() {
        return adapter != null && isDiscovering();
    }

    @Override
    public void startSearch(URoDiscoveryCallback<URoUkitBluetoothInfo> callback, long timeoutMs) {
        if(adapter == null) {
            if(callback != null) {
                callback.onDiscoveryFinish();
            }
            return;
        }
        if(callback == null) {
            return;
        }
        discoveryStart(callback, timeoutMs);
    }

    @Override
    public void stopSearch() {
        if(adapter == null) {
            return;
        }
        discoveryFinish();
    }

    @Override
    public void connect(URoUkitBluetoothInfo connectItem, long timeout, URoCompletionCallback<Void> callback) {
        connect(connectItem,timeout,callback,null);
    }

    public void connect(URoUkitBluetoothInfo connectItem, long timeout, URoCompletionCallback<Void> callback, UroProductInitDelegate initDelegate) {
        if(!connectReset()) {
            URoCompletionCallbackHelper.sendErrorCallback(URoError.NOT_ALLOWED, callback);
            return;
        }
        URoBluetoothDevice device = connectItem.device;
        String name = device.getName();
        URoCompletionCallbackWrap<?> callbackWarp = null;
        if(isJimuName(name)) {
            // fixme 蓝牙串口转蓝牙ble
            connectMission = new URoUkitLegacyConnectMission(connectItem);
            callbackWarp = new URoCompletionCallbackWrap<BluetoothSocket>(callback) {
                @Override
                public boolean onCompleteInternal(URoCompletionResult<BluetoothSocket> result) {
                    if(!result.isSuccess()) {
                        return true;
                    }
                    boolean resultBoolean = false;
                    try {
                        BluetoothSocket bluetoothSocket = result.getData();
                        URoLinkType linkType = URoLinkType.BLUETOOTH;
                        String linkName = connectItem.device.getName();
                        int rssi = connectItem.rssi;
                        URoBluetoothDevice device = connectItem.device;
                        URoUkitLegacyBtLinkModel linkModel = new URoUkitLegacyBtLinkModel(linkType, linkName, rssi, device);
                        URoUkitLegacyStreamLink link = new URoUkitLegacyStreamLink(linkModel, bluetoothSocket);
                        URoLinkRouter.getInstance().registerLink(link,initDelegate);
                        resultBoolean = true;
                    } catch (Throwable e) {
                        URoLogUtils.e(e);
                        URoIoUtils.close(result.getData());
                    }
                    return resultBoolean;
                }
            };
//            connectMission = new URoUkitLegacyBleConnectMission(connectItem);
//            callbackWarp = new URoCompletionCallbackWrap<URoUkitLegacyBleMgrDelegate>(callback) {
//                @Override
//                public boolean onCompleteInternal(URoCompletionResult<URoUkitLegacyBleMgrDelegate> result) {
//                    if(!result.isSuccess()) {
//                        return true;
//                    }
//                    boolean resultBoolean = false;
//                    try {
//                        URoUkitLegacyBleMgrDelegate delegate = result.getData();
//                        URoLinkType linkType = URoLinkType.BLUETOOTH;
//                        String linkName = connectItem.device.getName();
//                        int rssi = connectItem.rssi;
//                        BluetoothDevice device = connectItem.device;
//                        URoUkitLegacyBtLinkModel linkModel = new URoUkitLegacyBtLinkModel(linkType, linkName, rssi, device);
//                        URoUkitLegacyBleLink link = new URoUkitLegacyBleLink(linkModel, delegate);
//                        URoLinkRouter.getInstance().registerLink(link);
//                        resultBoolean = true;
//                    } catch (Throwable e) {
//                        URoLogUtils.e(e);
//                        result.getData().release();
//                    }
//                    return resultBoolean;
//                }
//            };
        } else if(isUKitName(name)) {
            connectMission = new URoUkitSmartBleConnectMission(connectItem);
            callbackWarp = new URoCompletionCallbackWrap<URoUkitSmartBleMgrDelegate>(callback) {
                @Override
                public boolean onCompleteInternal(URoCompletionResult<URoUkitSmartBleMgrDelegate> result) {
                    if(!result.isSuccess()) {
                        return true;
                    }
                    boolean resultBoolean = false;
                    try {
                        URoUkitSmartBleMgrDelegate delegate = result.getData();
                        URoLinkType linkType = URoLinkType.BLUETOOTH_BLE;
                        String linkName = connectItem.device.getName();
                        int rssi = connectItem.rssi;
                        URoBluetoothDevice device = connectItem.device;
                        URoUkitSmartBtLinkModel linkModel = new URoUkitSmartBtLinkModel(linkType, linkName, rssi, device);
                        URoUkitSmartBleLink link = new URoUkitSmartBleLink(linkModel, delegate);
                        URoLinkRouter.getInstance().registerLink(link,initDelegate);
                        resultBoolean = true;
                    } catch (Throwable e) {
                        URoLogUtils.e(e);
                        result.getData().release();
                    }
                    return resultBoolean;
                }
            };
        }
        if(connectMission == null) {
            URoCompletionCallbackHelper.sendErrorCallback(URoError.UNKNOWN, callback);
            return;
        }
        connectMission.setTimeout(timeout);
        connectMission.setCallback(callbackWarp);
        connectMission.start();
    }

    @Override
    protected boolean connectReset() {
        if(connectMission != null && connectMission.stop()) {
            connectMission = null;
            return true;
        }
        return true;
    }

    private void discoveryDeviceFound(URoUkitBluetoothInfo connectItem) {
        handler.post(() -> {
            if(callback != null) {
                callback.onDeviceFound(connectItem);
            }
        });
    }

    private void startDiscovery() {
        adapter.startDiscovery();
    }

    private void cancelDiscovery() {
        adapter.cancelDiscovery();
    }

    private boolean isDiscovering() {
        return adapter != null && adapter.isDiscovering();
    }

//    private BluetoothLeScanner scanner;
//    private ScanCallback scanCallback;

//    private void startDiscovery() {
//        if(adapter == null) {
//            return;
//        }
//        if(scanner != null) {
//            return;
//        }
//        synchronized (this) {
//            scanner = adapter.getBluetoothLeScanner();
//            ScanFilter.Builder scanFilterBuilder = new ScanFilter.Builder();
//            scanFilterBuilder.setServiceUuid(new ParcelUuid(UUID.fromString("00000001-0000-1000-8000-00805f9b34fb")));
//            ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder();
//            ArrayList<ScanFilter> filters = new ArrayList<>();
//            filters.add(scanFilterBuilder.build());
//            ScanSettings settings = scanSettingsBuilder.build();
//            scanCallback = new ScanCallback() {
//                @Override
//                public void onScanResult(int callbackType, ScanResult result) {
//                    final BluetoothDevice device = result.getDevice();
//                    final int rssi = result.getRssi();
////                    if(!isMatchName(device.getName())) {
////                        return;
////                    }
//                    URoLogUtils.d("Found device: %s[%s]", device.getName(), device.getAddress());
//                    if(callback != null) {
//                        discoveryDeviceFound(new URoUkitBluetoothInfo(rssi, device));
//                    }
//                }
//
//                @Override
//                public void onBatchScanResults(List<ScanResult> results) {
//                    for (ScanResult result : results) {
//                        final BluetoothDevice device = result.getDevice();
//                        final int rssi = result.getRssi();
////                        if(!isMatchName(device.getName())) {
////                            return;
////                        }
//                        URoLogUtils.d("Found device: %s[%s]", device.getName(), device.getAddress());
//                        if(callback != null) {
//                            discoveryDeviceFound(new URoUkitBluetoothInfo(rssi, device));
//                        }
//                    }
//                }
//
//                @Override
//                public void onScanFailed(int errorCode) {
//                    super.onScanFailed(errorCode);
//                    URoLogUtils.d("Discovery Finished");
//                    if(callback != null) {
//                        discoveryFinish();
//                    }
//                }
//            };
//            scanner.startScan(filters, settings, scanCallback);
//        }
//    }

//    private void cancelDiscovery() {
//        synchronized (this) {
//            if (scanner != null) {
//                scanner.stopScan(scanCallback);
//                scanner = null;
//            }
//            if(callback != null) {
//                URoLogUtils.d("Discovery Finished");
//                discoveryFinish();
//            }
//        }
//    }

//    private boolean isDiscovering() {
//        synchronized (this) {
//            return scanner != null;
//        }
//    }

    private void discoveryStart(URoDiscoveryCallback<URoUkitBluetoothInfo> callback, long timeoutMs) {
        if(isDiscovering()) {
            cancelDiscovery();
            final URoDiscoveryCallback<URoUkitBluetoothInfo> previousCallback = this.callback;
            handler.post(() -> {
                if(previousCallback != null) {
                    previousCallback.onDiscoveryFinish();
                }
            });
        }
        this.callback = callback;
        registerDiscoveryReceiver();
        startDiscovery();
        handler.removeCallbacks(discoveryTimeoutRunnable);
        handler.postDelayed(discoveryTimeoutRunnable, timeoutMs);
    }

    private void discoveryFinish() {
        handler.removeCallbacks(discoveryTimeoutRunnable);
        if (isDiscovering()) {
            cancelDiscovery();
        }
        unRegisterDiscoveryReceiver();
        handler.post(() -> {
            if(callback != null) {
                callback.onDiscoveryFinish();
                callback = null;
            }
        });
    }

    private void registerDiscoveryReceiver() {
        synchronized (discoveryLock) {
            if (discoveryReceiver != null) {
                return;
            }
            discoveryReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    switch (intent.getAction()) {
                    case BluetoothDevice.ACTION_FOUND:
                        if(callback != null) {
                            final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            final int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, (short)-100);
                            if(!isMatchName(device.getName())) {
                                return;
                            }
                            URoLogUtils.d("Found device: %s[%s]", device.getName(), device.getAddress());
                            discoveryDeviceFound(new URoUkitBluetoothInfo(rssi, device));
                        }
                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        if(callback != null) {
                            URoLogUtils.d("Discovery Finished");
                            discoveryFinish();
                        }
                        break;
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            URoSDK.getInstance().getContext().registerReceiver(discoveryReceiver, intentFilter);
        }
    }

    private void unRegisterDiscoveryReceiver() {
        synchronized (discoveryLock) {
            if (discoveryReceiver == null) {
                return;
            }
            URoSDK.getInstance().getContext().unregisterReceiver(discoveryReceiver);
            discoveryReceiver = null;
        }
    }

    private boolean isMatchName(String name) {
        return isJimuName(name) || isUKitName(name);
    }

    private boolean isJimuName(String name) {
        return !TextUtils.isEmpty(name) && (name.toLowerCase().startsWith("jimu_") || TextUtils.equals(name.toLowerCase(), "jimu"));
    }

    private boolean isUKitName(String name) {
        return !TextUtils.isEmpty(name) && name.startsWith("uKit2_");
    }

}
