package com.ubtedu.deviceconnect.libs.base.link;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Author naOKi
 * @Date 2019/08/13
 **/
public class URoBluetoothGattStreamInitiative<D extends URoBleMgrDelegate> extends URoIoStream<D> {

    private PipedTunnel receivePipedTunnel;

    public URoBluetoothGattStreamInitiative(@NonNull D source) throws Exception {
        super(source);
        receivePipedTunnel = new PipedTunnel();
        receivePipedTunnel.open();
        source.setDataReceivedCallback(new URoBluetoothGattDataReceivedCallback() {
            @Override
            public void onReceiveData(byte[] data) {
                if (receivePipedTunnel == null) {
                    return;
                }
                receivePipedTunnel.write(data);
            }
        });
    }

    @Override
    public int write(byte[] data, int offset, int length) {
        getSource().sendToDevice(data, offset, length);
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
        return getSource().isConnected();
    }

    @Override
    protected boolean disconnectInternal() throws IOException {
        if(receivePipedTunnel != null) {
            receivePipedTunnel.close();
            receivePipedTunnel = null;
        }
        getSource().release();
        return true;
    }

}
