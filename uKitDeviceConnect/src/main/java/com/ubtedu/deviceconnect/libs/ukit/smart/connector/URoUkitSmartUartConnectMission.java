package com.ubtedu.deviceconnect.libs.ukit.smart.connector;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import androidx.annotation.NonNull;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.ubtedu.deviceconnect.libs.base.URoCompletionCallback;
import com.ubtedu.deviceconnect.libs.base.URoSDK;
import com.ubtedu.deviceconnect.libs.base.connector.URoConnectMission;
import com.ubtedu.deviceconnect.libs.ukit.connector.URoUkitUartInfo;
import com.ubtedu.deviceconnect.libs.utils.URoIoUtils;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

import java.net.ConnectException;

/**
 * @Author naOKi
 * @Date 2019/08/15
 **/
public class URoUkitSmartUartConnectMission extends URoConnectMission<URoUkitUartInfo, UsbSerialPort> {

    private final Object lock = new Object();
    private Boolean hasError = null;

    public URoUkitSmartUartConnectMission(URoUkitUartInfo connectItem) {
        super(connectItem);
    }

    public URoUkitSmartUartConnectMission(URoUkitUartInfo connectItem, @NonNull URoCompletionCallback<UsbSerialPort> callback) {
        super(connectItem, callback);
    }

    @Override
    protected UsbSerialPort connect(URoUkitUartInfo connectItem) throws Exception {
        UsbDevice device = connectItem.device;
        UsbSerialDriver driver = UsbSerialProber.getDefaultProber().probeDevice(device);
        if(driver == null) {
            URoLogUtils.e("connection failed: no driver for device");
            throw new ConnectException();
        }
        UsbManager usbManager = (UsbManager)URoSDK.getInstance().getContext().getSystemService(Context.USB_SERVICE);
        UsbDeviceConnection usbConnection = usbManager.openDevice(device);
        if(usbConnection == null) {
            if(!usbManager.hasPermission(device)) {
                URoLogUtils.e("connection failed: permission denied");
            } else {
                URoLogUtils.e("connection failed: open failed");
            }
            throw new ConnectException();
        }
        if(driver.getPorts().size() != 1) {
            URoLogUtils.e("connection failed: no serial port found");
            throw new ConnectException();
        }
        UsbSerialPort serialPort = null;
        try {
            serialPort = driver.getPorts().get(0);
            serialPort.open(usbConnection);
            serialPort.setDTR(false);
            serialPort.setRTS(false);
            serialPort.setParameters(115200, UsbSerialPort.DATABITS_8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            return serialPort;
        } catch (Throwable e) {
            URoIoUtils.close(serialPort);
            throw new ConnectException();
        }
    }

    @Override
    protected void disconnect(UsbSerialPort serialPort) {
        URoIoUtils.close(serialPort);
    }

}
