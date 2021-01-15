package com.ubtedu.deviceconnect.libs.base.link;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.URoSDK;
import com.ubtedu.deviceconnect.libs.utils.URoIoUtils;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Deque;
import java.util.concurrent.LinkedBlockingQueue;

import no.nordicsemi.android.ble.Request;
import no.nordicsemi.android.ble.callback.DataReceivedCallback;
import no.nordicsemi.android.ble.data.Data;

/**
 * @Author naOKi
 * @Date 2019/08/15
 **/
public abstract class URoBleMgrDelegate implements DataReceivedCallback {

    private URoBleMgr<? extends URoBleMgrDelegate> bleMgr;
    private static final Object mBleMgrLock = new Object();

    private SendThread sendThread;

    private URoBluetoothGattDataReceivedCallback dataReceivedCallback = null;
    private URoBluetoothGattConnectChangedCallback connectChangedCallback = null;

    private LinkedBlockingQueue<byte[]> sendQueue = new LinkedBlockingQueue<>();

    protected boolean isRequiredServiceSupported(@NonNull BluetoothGatt gatt) {
        return false;
    }

    protected void onDeviceDisconnected() {
        // Stub.
    }

    protected boolean isOptionalServiceSupported(@NonNull BluetoothGatt gatt) {
        return false;
    }

    public void setBleMgr(URoBleMgr<? extends URoBleMgrDelegate> bleMgr) {
        this.bleMgr = bleMgr;
        if(sendThread == null) {
            sendThread = new SendThread();
            sendThread.start();
        }
    }

    protected void setDataReceivedCallback(URoBluetoothGattDataReceivedCallback dataReceivedCallback) {
        this.dataReceivedCallback = dataReceivedCallback;
    }

    protected void setConnectChangedCallback(URoBluetoothGattConnectChangedCallback connectChangedCallback) {
        this.connectChangedCallback = connectChangedCallback;
    }

    protected URoBleMgr<? extends URoBleMgrDelegate> getBleMgr() {
        return bleMgr;
    }

    protected void initialize() {
        // Stub.
    }

    protected void initializeDataReceivedCallback() {
        if(bleMgr == null) {
            return;
        }
        BluetoothGattCharacteristic characteristic = getNotifyGattCharacteristic();
        if(characteristic == null) {
            return;
        }
        bleMgr.callSetNotificationCallback(characteristic, this);
        bleMgr.callEnableNotifications(characteristic);
    }

    @Override
    public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {
        if(URoSDK.getInstance().isDebug()) {
            URoLogUtils.d("onDataReceived: %d", data.getValue() == null ? 0 : data.getValue().length);
        }
        if(dataReceivedCallback != null) {
            dataReceivedCallback.onReceiveData(data.getValue());
        }
    }

    protected void onDeviceReady() {
        // Stub.
        if(URoSDK.getInstance().isDebug()) {
            URoLogUtils.d("onDeviceReady");
        }
    }

    protected void onManagerReady() {
        // Stub.
        if(URoSDK.getInstance().isDebug()) {
            URoLogUtils.d("onManagerReady");
        }
    }

    @Deprecated
    protected void cancelQueue() {
        // Stub.
        if(URoSDK.getInstance().isDebug()) {
            URoLogUtils.d("cancelQueue");
        }
    }

    protected void onDeviceConnecting(@NonNull BluetoothDevice device) {
        // Stub.
        if(URoSDK.getInstance().isDebug()) {
            URoLogUtils.d("onDeviceConnecting");
        }
    }

    protected void onDeviceConnected(@NonNull BluetoothDevice device) {
        if(URoSDK.getInstance().isDebug()) {
            URoLogUtils.d("onDeviceConnected");
        }
        if(connectChangedCallback != null) {
            connectChangedCallback.onConnectStatusChanged(true);
        }
    }

    protected void onDeviceDisconnecting(@NonNull BluetoothDevice device) {
        // Stub.
        if(URoSDK.getInstance().isDebug()) {
            URoLogUtils.d("onDeviceDisconnecting");
        }
    }

    public void onDeviceFailedToConnect(@NonNull BluetoothDevice device, int reason) {
        if(URoSDK.getInstance().isDebug()) {
            URoLogUtils.d("onDeviceFailedToConnect: reason=%d", reason);
        }
    }

    protected void onDeviceDisconnected(@NonNull BluetoothDevice device, int reason) {
        if(URoSDK.getInstance().isDebug()) {
            URoLogUtils.d("onDeviceDisconnected: reason=%d", reason);
        }
        onDeviceDisconnected(device);
    }

    @Deprecated
    protected void onDeviceDisconnected(@NonNull BluetoothDevice device) {
        if(URoSDK.getInstance().isDebug()) {
            URoLogUtils.d("onDeviceDisconnected");
        }
        if(connectChangedCallback != null) {
            connectChangedCallback.onConnectStatusChanged(false);
        }
    }

    protected void onLinkLossOccurred(@NonNull BluetoothDevice device) {
        // Stub.
        if(URoSDK.getInstance().isDebug()) {
            URoLogUtils.d("onLinkLossOccurred");
        }
    }

    protected void onServicesDiscovered(@NonNull BluetoothDevice device, boolean optionalServicesFound) {
        // Stub.
        if(URoSDK.getInstance().isDebug()) {
            URoLogUtils.d("onServicesDiscovered");
        }
    }

    protected void onDeviceReady(@NonNull BluetoothDevice device) {
        // Stub.
        if(URoSDK.getInstance().isDebug()) {
            URoLogUtils.d("onDeviceReady");
        }
    }

    protected void onBondingRequired(@NonNull BluetoothDevice device) {
        // Stub.
        if(URoSDK.getInstance().isDebug()) {
            URoLogUtils.d("onBondingRequired");
        }
    }

    protected void onBonded(@NonNull BluetoothDevice device) {
        // Stub.
        if(URoSDK.getInstance().isDebug()) {
            URoLogUtils.d("onBonded");
        }
    }

    protected void onBondingFailed(@NonNull BluetoothDevice device) {
        // Stub.
        if(URoSDK.getInstance().isDebug()) {
            URoLogUtils.d("onBondingFailed");
        }
    }

    protected void onError(@NonNull BluetoothDevice device, @NonNull String message, int errorCode) {
        // Stub.
        if(URoSDK.getInstance().isDebug()) {
            URoLogUtils.d("onError: %s %d", message, errorCode);
        }
    }

    protected void onDeviceNotSupported(@NonNull BluetoothDevice device) {
        // Stub.
    }

    @Deprecated
    protected boolean shouldEnableBatteryLevelNotifications(@NonNull BluetoothDevice device) {
        return false;
    }

    @Deprecated
    protected void onBatteryValueReceived(@NonNull BluetoothDevice device, int value) {
        // Stub.
        if(URoSDK.getInstance().isDebug()) {
            URoLogUtils.d("onBatteryValueReceived: %d", value);
        }
    }

    @Deprecated
    protected Deque<Request> initGatt(@NonNull BluetoothGatt gatt) {
        return null;
    }

    @Deprecated
    protected void onCharacteristicRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic) {
        // Stub.
        if(URoSDK.getInstance().isDebug()) {
            URoLogUtils.d("onCharacteristicRead: %d", characteristic.getValue().length);
        }
    }

    @Deprecated
    protected void onCharacteristicWrite(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic) {
        // Stub.
        if(URoSDK.getInstance().isDebug()) {
            URoLogUtils.d("onCharacteristicWrite: %d", characteristic.getValue().length);
        }
    }

    @Deprecated
    protected void onDescriptorRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattDescriptor descriptor) {
        // Stub.
        if(URoSDK.getInstance().isDebug()) {
            URoLogUtils.d("onDescriptorRead");
        }
    }

    @Deprecated
    protected void onDescriptorWrite(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattDescriptor descriptor) {
        // Stub.
        if(URoSDK.getInstance().isDebug()) {
            URoLogUtils.d("onDescriptorWrite");
        }
    }

    @Deprecated
    protected void onBatteryValueReceived(@NonNull BluetoothGatt gatt, int value) {
        // Stub.
        if(URoSDK.getInstance().isDebug()) {
            URoLogUtils.d("onBatteryValueReceived: %d", value);
        }
    }

    @Deprecated
    protected void onCharacteristicNotified(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic) {
        // Stub.
        if(URoSDK.getInstance().isDebug()) {
            URoLogUtils.d("onCharacteristicNotified: %d", characteristic.getValue().length);
        }
    }

    @Deprecated
    protected void onCharacteristicIndicated(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic) {
        // Stub.
        if(URoSDK.getInstance().isDebug()) {
            URoLogUtils.d("onCharacteristicIndicated: %d", characteristic.getValue().length);
        }
    }

    @Deprecated
    protected void onMtuChanged(@NonNull BluetoothGatt gatt, int mtu) {
        // Stub.
        if(URoSDK.getInstance().isDebug()) {
            URoLogUtils.d("onMtuChanged: %d", mtu);
        }
    }

    @Deprecated
    protected void onConnectionUpdated(@NonNull BluetoothGatt gatt, int interval, int latency, int timeout) {
        // Stub.
        if(URoSDK.getInstance().isDebug()) {
            URoLogUtils.d("onConnectionUpdated: %d, %d, %d", interval, latency, timeout);
        }
    }

    protected void log(int priority, @NonNull String message) {
        if(TextUtils.isEmpty(message)) {
            return;
        }
//        if(message.startsWith("Writing characteristic")
//                || message.startsWith("gatt.writeCharacteristic")
//                || message.startsWith("Data written to")
//            /*|| message.startsWith("Notification received from")*/) {
//            return;
//        }
        if(URoSDK.getInstance().isDebug()) {
            if (priority >= Log.ERROR) {
                URoLogUtils.e(message);
//                URoLogUtils.e(new Exception("ERROR"));
            } else if (priority >= Log.WARN) {
                URoLogUtils.w(message);
            } else if (priority >= Log.VERBOSE) {
                URoLogUtils.d(message);
            }
        }
    }

    protected void sendToDevice(byte[] data, int offset, int length) {
        byte[] buffer = new byte[length];
        System.arraycopy(data, offset, buffer, 0, length);
        sendQueue.offer(buffer);
//        synchronized (mBleMgrLock) {
//            if(bleMgr == null) {
//                URoLogUtils.e("Empty ble Message!");
//                return;
//            }
//            BluetoothGattCharacteristic characteristic = getWriteGattCharacteristic();
//            if (characteristic == null) {
//                URoLogUtils.e("Write characteristic is NULL!");
//                return;
//            }
//            bleMgr.callWriteCharacteristic(characteristic, data, offset, length);
//        }
    }

    public boolean isConnected() {
        return bleMgr != null && getBleMgr().isConnected();
    }

//    public void close() {
//        if(bleMgr == null) {
//            return;
//        }
//        bleMgr.close();
//        bleMgr = null;
//    }

    public void release() {
        if(sendThread != null) {
            sendThread.doStop();
            sendThread = null;
        }
        if(bleMgr == null) {
            return;
        }
        sendQueue.clear();
        bleMgr.release();
        bleMgr = null;
    }

    protected abstract BluetoothGattCharacteristic getWriteGattCharacteristic();
    protected abstract BluetoothGattCharacteristic getNotifyGattCharacteristic();

    private class SendThread extends Thread {
        private boolean stop = false;

        public SendThread() {
            super("Thread-SendThread");
        }

        private void sendToTarget(byte[] data) {
            synchronized (mBleMgrLock) {
                if(bleMgr == null) {
                    URoLogUtils.e("Empty ble Message!");
                    return;
                }
                BluetoothGattCharacteristic characteristic = getWriteGattCharacteristic();
                if (characteristic == null) {
                    URoLogUtils.e("Write characteristic is NULL!");
                    return;
                }
                bleMgr.callWriteCharacteristic(characteristic, data, 0, data.length);
//                int readLen = 0;
//                while(readLen < data.length) {
//                    int partLen = Math.min(data.length - readLen, 20);
//                    bleMgr.callWriteCharacteristic(characteristic, data, readLen, partLen);
//                    readLen += partLen;
//                }
            }
        }

        private int getTotalLength(ArrayList<byte[]> sendList) {
            int total = 0;
            for(byte[] data : sendList) {
                total += data.length;
            }
            return total;
        }

        private byte[] getMergeData(ArrayList<byte[]> sendList) {
            if(sendList != null && sendList.size() == 1) {
                return sendList.get(0) != null ? sendList.get(0) : new byte[0];
            }
            ByteArrayOutputStream baos = null;
            byte[] result;
            try {
                baos = new ByteArrayOutputStream();
                for (byte[] data : sendList) {
                    baos.write(data);
                }
                result = baos.toByteArray();
                baos.reset();
            } catch (Throwable e) {
                result = new byte[0];
            } finally {
                URoIoUtils.close(baos);
            }
            return result;
        }

        @Override
        public void run() {
            ArrayList<byte[]> sendList = new ArrayList<>();
            while(!stop && bleMgr != null) {
                try {
                    byte[] data = sendQueue.peek();
                    int mtu = bleMgr.callGetMtu();
                    if(mtu == 23) {
                        if(data == null) {
                            continue;
                        }
                        sendToTarget(data);
                        sendQueue.remove(data);
                    } else {
                        int listLength = getTotalLength(sendList);
                        if(data == null) {
                            if(listLength != 0) {
                                sendToTarget(getMergeData(sendList));
                                sendList.clear();
                            }
                            continue;
                        }
                        int dataLength = data.length;
                        int nextListLength = listLength + dataLength;
                        if (listLength != 0 && nextListLength > mtu) {
                            sendToTarget(getMergeData(sendList));
                            sendList.clear();
                            listLength = 0;
                            nextListLength = dataLength;
                        }
                        if (listLength == 0 || nextListLength < mtu) {
                            sendList.add(data);
                            sendQueue.remove(data);
                        }
                    }
                } catch (Exception e) {
                    URoLogUtils.e(e);
                }
            }
        }

        public void doStop() {
            stop = true;
        }
    }

}
