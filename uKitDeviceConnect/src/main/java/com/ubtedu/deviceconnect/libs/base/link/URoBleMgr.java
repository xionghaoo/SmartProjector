package com.ubtedu.deviceconnect.libs.base.link;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.URoCompletionCallbackHelper;
import com.ubtedu.deviceconnect.libs.base.URoSDK;
import com.ubtedu.deviceconnect.libs.base.model.URoError;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

import java.lang.ref.WeakReference;
import java.util.Deque;

import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.BleManagerCallbacks;
import no.nordicsemi.android.ble.Request;
import no.nordicsemi.android.ble.annotation.ConnectionPriority;
import no.nordicsemi.android.ble.callback.DataReceivedCallback;
import no.nordicsemi.android.ble.observer.ConnectionObserver;

/**
 * @Author naOKi
 * @Date 2019/08/15
 **/
public class URoBleMgr<D extends URoBleMgrDelegate> extends BleManager {

    private WeakReference<BluetoothGatt> gattReference = null;

    private BleManagerGattCallbackInternal callbackInternal;
    private final D delegate;
    private URoCompletionCallback<Void> initCallback;
    private Handler handler = new Handler(Looper.getMainLooper());

    public URoBleMgr(@NonNull D delegate) {
        super(URoSDK.getInstance().getContext());
        this.delegate = delegate;
        delegate.setBleMgr(this);
        ((BleManagerGattCallbackInternal)getGattCallback()).setDelegate(delegate);
        super.setGattCallbacks(new BleManagerCallbacksInternal(delegate));
//        super.setConnectionObserver(new BleManagerCallbacksInternal(delegate));
    }

    public BluetoothGatt getBluetoothGatt() {
        if(gattReference == null) {
            return null;
        }
        return gattReference.get();
    }

    public void setInitCallback(URoCompletionCallback<Void> initCallback) {
        this.initCallback = initCallback;
    }

    @NonNull
    @Override
    protected BleManagerGattCallback getGattCallback() {
        synchronized (this) {
            if (callbackInternal == null) {
                callbackInternal = new BleManagerGattCallbackInternal(null);
            }
            return callbackInternal;
        }
    }

    @Override
    protected boolean shouldClearCacheWhenDisconnected() {
        return true;
    }

    public void callWriteCharacteristic(@Nullable final BluetoothGattCharacteristic characteristic, @Nullable final byte[] data, final int offset, final int length) {
        writeCharacteristic(characteristic, data, offset, length)
                .split()
                .done((device) -> {
//                    URoLogUtils.e("发送成功");
                })
                .fail((device, status) -> {
                    URoLogUtils.e("发送失败");
                })
                .invalid(() -> {
                    URoLogUtils.e("无效数据");
                })
                .enqueue();

//        if(data == null) {
//            return;
//        }
//
//        ReliableWriteRequest reliableWriteRequest = beginReliableWrite();
//        int readLen = 0;
//        while(readLen < data.length) {
//            int partLen = Math.min(data.length - readLen, 20);
//            reliableWriteRequest.add(writeCharacteristic(characteristic, data, readLen, partLen));
//            readLen += partLen;
//        }
//        reliableWriteRequest.done((device) -> {
////                    URoLogUtils.e("发送成功");
//                })
//                .fail((device, status) -> {
//                    URoLogUtils.e("发送失败");
//                })
//                .invalid(() -> {
//                    URoLogUtils.e("无效数据");
//                });
//        reliableWriteRequest.enqueue();
    }

    public void callRefresh() {
        refreshDeviceCache().enqueue();
    }

    public void callRequestMtu(@IntRange(from = 23, to = 517) final int mtu) {
        requestMtu(mtu).enqueue();
    }

    public void callRequestConnectionPriority(@ConnectionPriority final int priority) {
        requestConnectionPriority(priority).enqueue();
    }

    public int callGetMtu() {
        return getMtu();
    }

    public void callSetNotificationCallback(@Nullable final BluetoothGattCharacteristic characteristic, @NonNull final DataReceivedCallback callback) {
        setNotificationCallback(characteristic).with(callback);
    }

    public void callEnableNotifications(@Nullable final BluetoothGattCharacteristic characteristic) {
        enableNotifications(characteristic).enqueue();
    }

//    @Override
//    public void close() {
//        if(Looper.getMainLooper() == Looper.myLooper()) {
//            super.close();
//        } else {
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    URoBleMgr.super.close();
//                }
//            });
//        }
//    }

    public void release() {
        if(isConnected()) {
            disconnect()
//                .done((device) -> {close();})
//                .fail((device, status) -> {close();})
//                .invalid(() -> {close();})
                    .enqueue();
        } else {
            close();
        }
    }

    @Override
    public void setGattCallbacks(@NonNull final BleManagerCallbacks callbacks) {
        // Stub!
    }

    @Override
    public void log(int priority, @NonNull String message) {
        if(delegate != null) {
            delegate.log(priority, message);
        }
    }

    private class BleManagerCallbacksInternal implements BleManagerCallbacks, ConnectionObserver {

        private URoBleMgrDelegate delegate;

        protected BleManagerCallbacksInternal(@NonNull URoBleMgrDelegate delegate) {
            this.delegate = delegate;
        }

        @Override
        public void onDeviceConnecting(@NonNull BluetoothDevice device) {
            if(delegate != null) {
                delegate.onDeviceConnecting(device);
            }
        }

        @Override
        public void onDeviceConnected(@NonNull BluetoothDevice device) {
            if(delegate != null) {
                delegate.onDeviceConnected(device);
            }
        }

        @Override
        public void onDeviceFailedToConnect(@NonNull BluetoothDevice device, int reason) {
            if(delegate != null) {
                delegate.onDeviceFailedToConnect(device, reason);
            }
        }

        @Override
        public void onDeviceDisconnecting(@NonNull BluetoothDevice device) {
            if(delegate != null) {
                delegate.onDeviceDisconnecting(device);
            }
        }

        @Override
        public void onDeviceDisconnected(@NonNull BluetoothDevice device, int reason) {
            if(delegate != null) {
                delegate.onDeviceDisconnected(device, reason);
            }
        }

        @Override
        public void onDeviceDisconnected(@NonNull BluetoothDevice device) {
            if(delegate != null) {
                delegate.onDeviceDisconnected(device);
            }
        }

        @Override
        public void onLinkLossOccurred(@NonNull BluetoothDevice device) {
            if(delegate != null) {
                delegate.onLinkLossOccurred(device);
            }
        }

        @Override
        public void onServicesDiscovered(@NonNull BluetoothDevice device, boolean optionalServicesFound) {
            if(delegate != null) {
                delegate.onServicesDiscovered(device, optionalServicesFound);
            }
        }

        @Override
        public void onDeviceReady(@NonNull BluetoothDevice device) {
            if(delegate != null) {
                delegate.onDeviceReady(device);
            }
        }

        @Override
        public void onBondingRequired(@NonNull BluetoothDevice device) {
            if(delegate != null) {
                delegate.onBondingRequired(device);
            }
        }

        @Override
        public void onBonded(@NonNull BluetoothDevice device) {
            if(delegate != null) {
                delegate.onBonded(device);
            }
        }

        @Override
        public void onBondingFailed(@NonNull BluetoothDevice device) {
            if(delegate != null) {
                delegate.onBondingFailed(device);
            }
        }

        @Override
        public void onError(@NonNull BluetoothDevice device, @NonNull String message, int errorCode) {
            if(delegate != null) {
                delegate.onError(device, message, errorCode);
            }
//            URoCompletionCallbackHelper.sendErrorCallback(URoError.INVALID, null);
//            initCallback = null;
        }

        @Override
        public void onDeviceNotSupported(@NonNull BluetoothDevice device) {
            if(delegate != null) {
                delegate.onDeviceNotSupported(device);
            }
            URoCompletionCallbackHelper.sendErrorCallback(URoError.INVALID, null);
            initCallback = null;
        }

        @Override
        public boolean shouldEnableBatteryLevelNotifications(@NonNull BluetoothDevice device) {
            return delegate != null && delegate.shouldEnableBatteryLevelNotifications(device);
        }

        @Override
        public void onBatteryValueReceived(@NonNull BluetoothDevice device, int value) {
            if(delegate != null) {
                delegate.onBatteryValueReceived(device, value);
            }
        }
    }

    private class BleManagerGattCallbackInternal extends BleManagerGattCallback {

        private URoBleMgrDelegate delegate;

        protected BleManagerGattCallbackInternal(@NonNull URoBleMgrDelegate delegate) {
            super();
            this.delegate = delegate;
        }

        public void setDelegate(URoBleMgrDelegate delegate) {
            this.delegate = delegate;
        }

        @Override
        protected boolean isRequiredServiceSupported(@NonNull BluetoothGatt gatt) {
            gattReference = new WeakReference<>(gatt);
//            List<BluetoothGattService> services = gatt.getServices();
//            for(BluetoothGattService service : services) {
//                URoLogUtils.d("[Service] uuid=%s", service.getUuid().toString());
//            }
            return delegate != null && delegate.isRequiredServiceSupported(gatt);
        }

        @Override
        protected void onDeviceDisconnected() {
            if(delegate != null) {
                delegate.onDeviceDisconnected();
            }
        }

        @Override
        protected boolean isOptionalServiceSupported(@NonNull BluetoothGatt gatt) {
            return delegate != null && delegate.isOptionalServiceSupported(gatt);
        }

        @Override
        protected void initialize() {
            if(delegate != null) {
                delegate.initialize();
            }
            if(delegate != null) {
                delegate.initializeDataReceivedCallback();
            }
        }

        @Override
        protected void onDeviceReady() {
            if(delegate != null) {
                delegate.onDeviceReady();
            }
            URoCompletionCallbackHelper.sendSuccessCallback(null, initCallback);
            initCallback = null;
        }

        @Override
        protected void onManagerReady() {
            if(delegate != null) {
                delegate.onManagerReady();
            }
        }

//        @Override
//        protected void cancelQueue() {
//            if(delegate != null) {
//                delegate.cancelQueue();
//            }
//        }

        @Override
        protected Deque<Request> initGatt(@NonNull BluetoothGatt gatt) {
            if(delegate != null) {
                return delegate.initGatt(gatt);
            }
            return null;
        }

        @Override
        protected void onCharacteristicRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic) {
            if(delegate != null) {
                delegate.onCharacteristicRead(gatt, characteristic);
            }
        }

        @Override
        protected void onCharacteristicWrite(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic) {
            if(delegate != null) {
                delegate.onCharacteristicWrite(gatt, characteristic);
            }
        }

        @Override
        protected void onDescriptorRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattDescriptor descriptor) {
            if(delegate != null) {
                delegate.onDescriptorRead(gatt, descriptor);
            }
        }

        @Override
        protected void onDescriptorWrite(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattDescriptor descriptor) {
            if(delegate != null) {
                delegate.onDescriptorWrite(gatt, descriptor);
            }
        }

        @Override
        protected void onBatteryValueReceived(@NonNull BluetoothGatt gatt, int value) {
            if(delegate != null) {
                delegate.onBatteryValueReceived(gatt, value);
            }
        }

        @Override
        protected void onCharacteristicNotified(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic) {
            if(delegate != null) {
                delegate.onCharacteristicNotified(gatt, characteristic);
            }
        }

        @Override
        protected void onCharacteristicIndicated(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic) {
            if(delegate != null) {
                delegate.onCharacteristicIndicated(gatt, characteristic);
            }
        }

        @Override
        protected void onMtuChanged(@NonNull BluetoothGatt gatt, int mtu) {
            if(delegate != null) {
                delegate.onMtuChanged(gatt, mtu);
            }
//            overrideMtu(23);
        }

        @Override
        protected void onConnectionUpdated(@NonNull BluetoothGatt gatt, int interval, int latency, int timeout) {
            if(delegate != null) {
                delegate.onConnectionUpdated(gatt, interval, latency, timeout);
            }
        }
    }

}
