package com.ubtedu.deviceconnect.libs.ukit.smart.connector;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.link.URoBleMgr;
import com.ubtedu.deviceconnect.libs.base.link.URoBleMgrDelegate;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

import java.lang.ref.WeakReference;
import java.util.UUID;

/**
 * @Author naOKi
 * @Date 2019/08/15
 **/
public class URoUkitSmartBleMgrDelegate extends URoBleMgrDelegate {

    private BluetoothGattCharacteristic writeGattCharacteristic = null;
    private BluetoothGattCharacteristic notifyGattCharacteristic = null;

    private WeakReference<BluetoothGatt> gattReference = null;

//    private static final UUID GATT_WRITE_SERVICE_UUID = UUID.fromString("00000001-0000-1000-8000-00805f9b34fb");
//    private static final UUID GATT_WRITE_CHARACTERISTIC_UUID = UUID.fromString("00000002-0000-1000-8000-00805f9b34fb");
//    private static final UUID GATT_NOTIFY_SERVICE_UUID = UUID.fromString("00000001-0000-1000-8000-00805f9b34fb");
//    private static final UUID GATT_NOTIFY_CHARACTERISTIC_UUID = UUID.fromString("00000003-0000-1000-8000-00805f9b34fb");

    private static final UUID GATT_WRITE_SERVICE_UUID = UUID.fromString("55425401-ff00-1000-8000-00805f9b34fb");
    private static final UUID GATT_WRITE_CHARACTERISTIC_UUID = UUID.fromString("55425402-ff00-1000-8000-00805f9b34fb");
    private static final UUID GATT_NOTIFY_SERVICE_UUID = UUID.fromString("55425401-ff00-1000-8000-00805f9b34fb");
    private static final UUID GATT_NOTIFY_CHARACTERISTIC_UUID = UUID.fromString("55425403-ff00-1000-8000-00805f9b34fb");

//    private static final UUID GATT_WRITE_SERVICE_UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
//    private static final UUID GATT_WRITE_CHARACTERISTIC_UUID = UUID.fromString("0000ffe3-0000-1000-8000-00805f9b34fb");
//    private static final UUID GATT_NOTIFY_SERVICE_UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
//    private static final UUID GATT_NOTIFY_CHARACTERISTIC_UUID = UUID.fromString("0000ffe2-0000-1000-8000-00805f9b34fb");

    @Override
    protected boolean isRequiredServiceSupported(@NonNull BluetoothGatt gatt) {
        BluetoothGattService gattServiceWrite = gatt.getService(GATT_WRITE_SERVICE_UUID);
        BluetoothGattService gattServiceNotify = gatt.getService(GATT_NOTIFY_SERVICE_UUID);
        if(gattServiceWrite == null || gattServiceNotify == null) {
            onConnectFailure("Not found service");
            return false;
        }
        writeGattCharacteristic = gattServiceWrite.getCharacteristic(GATT_WRITE_CHARACTERISTIC_UUID);
        notifyGattCharacteristic = gattServiceNotify.getCharacteristic(GATT_NOTIFY_CHARACTERISTIC_UUID);
        if(writeGattCharacteristic == null || notifyGattCharacteristic == null) {
            onConnectFailure("Not found characteristic");
            return false;
        }
        URoLogUtils.e("WriteGattCharacteristic: %s", writeGattCharacteristic.getUuid().toString());
        URoLogUtils.e("NotifyGattCharacteristic: %s", notifyGattCharacteristic.getUuid().toString());
        writeGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        notifyGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        gattReference = new WeakReference<>(gatt);
        return true;
    }

    @Override
    protected BluetoothGattCharacteristic getWriteGattCharacteristic() {
        return writeGattCharacteristic;
    }

    @Override
    protected BluetoothGattCharacteristic getNotifyGattCharacteristic() {
        return notifyGattCharacteristic;
    }

    @Override
    protected void onDeviceDisconnected() {
        release();
    }

    @Override
    protected void initialize() {
        URoBleMgr bleMgr = getBleMgr();
        if(bleMgr == null) {
            return;
        }
//        bleMgr.callRequestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
        bleMgr.callRequestMtu(203);
    }

    @Override
    protected void onDeviceReady() {
        super.onDeviceReady();
        URoLogUtils.e("Initialize: Done");
    }

    private void onConnectFailure(String msg) {
        URoLogUtils.e("Connect failure --> %s", msg);
        writeGattCharacteristic = null;
        notifyGattCharacteristic = null;
        release();
    }

}
