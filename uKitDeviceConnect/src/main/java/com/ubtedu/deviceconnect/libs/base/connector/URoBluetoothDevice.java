package com.ubtedu.deviceconnect.libs.base.connector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

/**
 * @Author naOKi
 * @Date 2020/01/21
 **/
public class URoBluetoothDevice {

    private final String address;
    private final String name;
    private final int rssi;

    public URoBluetoothDevice(BluetoothDevice device, int rssi) {
        this.address = device.getAddress();
        this.name = device.getName();
        this.rssi = rssi;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public int getRssi() {
        return rssi;
    }

    public BluetoothDevice getBluetoothDevice() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if(adapter != null) {
            return adapter.getRemoteDevice(getAddress());
        }
        return null;
    }

    public int getBondState() {
        BluetoothDevice device = getBluetoothDevice();
        if(device != null) {
            return device.getBondState();
        } else {
            return BluetoothDevice.BOND_NONE;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof URoBluetoothDevice)) return false;

        URoBluetoothDevice that = (URoBluetoothDevice) o;

        return getAddress() != null ? getAddress().equals(that.getAddress()) : that.getAddress() == null;
    }

    @Override
    public int hashCode() {
        return getAddress() != null ? getAddress().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "URoBluetoothDevice{" +
                "address='" + address + '\'' +
                ", name='" + name + '\'' +
                ", rssi=" + rssi +
                '}';
    }

}
