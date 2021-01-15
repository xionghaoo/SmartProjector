package com.ubtedu.deviceconnect.libs.base.link;

import androidx.annotation.NonNull;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.ubtedu.deviceconnect.libs.utils.URoIoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author naOKi
 * @Date 2019/08/13
 **/
public class URoUsbDeviceStream extends URoIoStream<UsbSerialPort> {

    private PipedTunnel receivePipedTunnel;
    private LinkedBlockingQueue<byte[]> messageQueue = new LinkedBlockingQueue<>();
    private Thread scheduler;
    private byte[] buffer = new byte[4 * 1024];
    private boolean isRunning = false;

    private URoBluetoothGattConnectChangedCallback connectChangedCallback = null;

    public URoUsbDeviceStream(@NonNull UsbSerialPort source) throws Exception {
        super(source);
        receivePipedTunnel = new PipedTunnel();
        receivePipedTunnel.open();
        isRunning = true;
        scheduler = new Thread("usb_device_scheduler_thread") {
            @Override
            public void run() {
                try {
                    while(isRunning) {
                        step();
                    }
                } catch (Throwable e) {
                    try {
                        disconnectInternal();
                    } catch (Throwable ex) {
                        // do nothing
                    }
                }
            }

            private void step() throws Throwable {
                int readLen = source.read(buffer, 0);
                if(readLen > 0) {
//                    URoLogUtils.e("Read data len=%d", readLen);
                    receivePipedTunnel.write(buffer, 0, readLen);
                }
                byte[] data = messageQueue.poll();
                if(data != null) {
                    int writeTotal = 0;
                    int dataLength = data.length;
                    while (true) {
                        int writeLen = source.write(data, 0);
//                        URoLogUtils.e("Write data len=%d", writeLen);
                        if (writeLen <= 0) {
                            throw new IOException("Write Error");
                        }
                        writeTotal += writeLen;
                        if (writeTotal != dataLength) {
                            data = Arrays.copyOfRange(data, writeLen, data.length);
                            continue;
                        }
                        break;
                    }
                }
            }
        };
        scheduler.start();
    }

    @Override
    public int write(byte[] data, int offset, int length) {
        messageQueue.offer(Arrays.copyOfRange(data, offset, offset + length));
        return 0;
    }

    @Override
    protected InputStream getInputStream() throws IOException {
        return receivePipedTunnel.getInputStream();
    }

    @Override
    protected OutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public boolean isConnected() {
        return getSource().isOpen();
    }

    public void setConnectChangedCallback(URoBluetoothGattConnectChangedCallback connectChangedCallback) {
        this.connectChangedCallback = connectChangedCallback;
    }

    @Override
    protected boolean disconnectInternal() throws IOException {
        isRunning = false;
        if(scheduler != null) {
            scheduler.interrupt();
            scheduler = null;
        }
        if(receivePipedTunnel != null) {
            receivePipedTunnel.close();
            receivePipedTunnel = null;
        }
        if(messageQueue != null) {
            messageQueue.clear();
            messageQueue = null;
        }
        URoIoUtils.close(getSource());
        if(connectChangedCallback != null) {
            URoBluetoothGattConnectChangedCallback _connectChangedCallback = connectChangedCallback;
            connectChangedCallback = null;
            _connectChangedCallback.onConnectStatusChanged(false);
        }
        return true;
    }

}
