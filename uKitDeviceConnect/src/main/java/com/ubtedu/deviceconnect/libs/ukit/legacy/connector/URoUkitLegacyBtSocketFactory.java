package com.ubtedu.deviceconnect.libs.ukit.legacy.connector;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @Author naOKi
 **/
public class URoUkitLegacyBtSocketFactory {

    private URoUkitLegacyBtSocketFactory() {}

    private static final UUID UKIT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static BluetoothSocket createSocketUseFixChannel(BluetoothDevice device) throws Exception {
        //虽然使用了反射，但是Android9.0上面测试OK
        Method m = device.getClass().getMethod("createInsecureRfcommSocket", int.class);
        return (BluetoothSocket)m.invoke(device, 6);
    }

    private static BluetoothSocket createSocketUseSecure(BluetoothDevice device) throws Exception {
        return device.createRfcommSocketToServiceRecord(UKIT_UUID);
    }

    private static BluetoothSocket createSocketUseInsecure(BluetoothDevice device) throws Exception {
        return device.createInsecureRfcommSocketToServiceRecord(UKIT_UUID);
    }

    public static BluetoothSocket createSocket(BluetoothDevice device) throws Exception {
        if((Build.VERSION.RELEASE.startsWith("4.0.") && (Build.MANUFACTURER.equals("samsung") || Build.MANUFACTURER.equals("HTC")))
                || (Build.VERSION.RELEASE.startsWith("4.1.") && Build.MANUFACTURER.equals("samsung"))
                || (Build.VERSION.RELEASE.equals("2.3.5") && Build.MANUFACTURER.equals("Xiaomi"))) {
            return createSocketUseFixChannel(device);
        }

        if((Build.MANUFACTURER.equals("Xiaomi") && (Build.MODEL.equals("2013022") && Build.VERSION.RELEASE.equals("4.2.1")))
                || Build.MODEL.equals("Lenovo A820") || Build.MODEL.equals("UBT-R800")) {
            return createSocketUseSecure(device);
        }

        return createSocketUseInsecure(device);
    }

}
