package com.ubtedu.deviceconnect.libs.base.link;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @Author naOKi
 * @Date 2019/08/13
 **/
public class URoTcpStream extends URoIoStream<Socket> {

    public URoTcpStream(@NonNull Socket source) throws Exception {
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
