package com.ubtedu.deviceconnect.libs.base.link;

import android.bluetooth.BluetoothSocket;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Author naOKi
 * @Date 2019/08/13
 **/
public class URoBluetoothSocketStream extends URoIoStream<BluetoothSocket> {

    public URoBluetoothSocketStream(@NonNull BluetoothSocket source) throws Exception {
        super(source);
    }

    @Override
    protected InputStream getInputStream() throws IOException {
        return getSource().getInputStream();
    }

    @Override
    protected OutputStream getOutputStream() throws IOException {
        return getSource().getOutputStream();
    }

    @Override
    public boolean isConnected() {
        return getSource().isConnected();
    }

    @Override
    protected boolean disconnectInternal() throws IOException {
        getSource().close();
        return true;
    }

}
