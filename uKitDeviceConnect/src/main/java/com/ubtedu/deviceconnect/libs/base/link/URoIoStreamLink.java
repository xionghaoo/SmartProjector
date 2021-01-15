package com.ubtedu.deviceconnect.libs.base.link;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.model.URoLinkModel;
import com.ubtedu.deviceconnect.libs.base.product.URoConnectStatus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * @Author naOKi
 * @Date 2019/08/13
 **/
public abstract class URoIoStreamLink<T> extends URoLink {

    private URoIoStream<T> source;
    private InputStream is;
    private OutputStream os;

    private InitiativeReceiveTunnel receiveTunnel = null;

    public <D extends URoIoStream<T>> URoIoStreamLink(@NonNull URoLinkModel model, @NonNull D source) throws Exception {
        super(model);
        this.source = source;
        this.is = source.getInputStream();
        this.os = source.getOutputStream();
    }

    public URoIoStream<T> getSource() {
        return source;
    }

    @Override
    protected void writeInternal(byte[] b, int off, int len) throws Exception {
        if(os == null) {
            source.write(b, off, len);
        } else {
            os.write(b, off, len);
        }
    }

    @Override
    protected int readInternal(byte[] b, int off, int len) throws Exception {
        if(is == null) {
            return source.read(b, off, len);
        } else {
            try {
                return is.read(b, off, len);
            } catch (IOException e) {
                onConnectivityChange(this, URoConnectStatus.DISCONNECTED);
                disconnect();
                throw e;
            }
        }
    }

    @Override
    public boolean isConnected() {
        return source.isConnected();
    }

    @Override
    protected boolean disconnectInternal() throws Exception {
        if(receiveTunnel != null) {
            receiveTunnel.close();
            receiveTunnel = null;
        }
        return source.disconnect();
    }

    @Override
    protected void initReceiveTunnel() {
        if (URoLinkReceiveMode.PASSIVE.equals(getLinkReceiveMode())) {
            receiveTunnel = new InitiativeReceiveTunnel(is);
            receiveTunnel.open();
        }
    }

    private class InitiativeReceiveTunnel extends Thread {

        private InputStream is;

        private InitiativeReceiveTunnel(InputStream is) {
            super("InitiativeReceiveTunnel");
            this.is = is;
        }

        private final static int BUFFER_LENGTH = 8 * 1024;
        private boolean isStopped = false;

        @Override
        public void run() {
            byte[] buffer = new byte[BUFFER_LENGTH];
            int readLen;
            while(!isStopped) {
                try {
                    if ((readLen = is.read(buffer, 0, BUFFER_LENGTH)) > 0) {
                        byte[] data = Arrays.copyOfRange(buffer, 0, readLen);
                        onReceiveData(URoIoStreamLink.this, data);
                    }
                } catch (IOException e) {
                    onConnectivityChange(URoIoStreamLink.this, URoConnectStatus.DISCONNECTED);
                    disconnect();
                } catch (Exception e) {
                    // ignore
                }
            }
        }

        public void close() {
            isStopped = true;
            interrupt();
        }

        public void open() {
            start();
        }

    }

}
