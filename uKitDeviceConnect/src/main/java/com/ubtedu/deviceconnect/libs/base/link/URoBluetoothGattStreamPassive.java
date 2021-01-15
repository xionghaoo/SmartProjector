package com.ubtedu.deviceconnect.libs.base.link;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Author naOKi
 * @Date 2019/08/13
 **/
public class URoBluetoothGattStreamPassive<D extends URoBleMgrDelegate> extends URoIoStream<D> {

    public URoBluetoothGattStreamPassive(@NonNull D source) throws Exception {
        super(source);
    }

    @Override
    public int write(byte[] data, int offset, int length) {
        getSource().sendToDevice(data, offset, length);
        return 0;
    }

    @Override
    protected InputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    protected OutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public boolean isConnected() {
        return getSource().isConnected();
    }

    @Override
    protected boolean disconnectInternal() throws IOException {
        getSource().release();
        return true;
    }

}
