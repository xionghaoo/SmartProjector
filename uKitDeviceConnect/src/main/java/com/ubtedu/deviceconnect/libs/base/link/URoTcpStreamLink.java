package com.ubtedu.deviceconnect.libs.base.link;

import androidx.annotation.NonNull;

import com.ubtedu.deviceconnect.libs.base.model.URoLinkModel;

import java.net.Socket;

/**
 * @Author naOKi
 * @Date 2019/08/13
 **/
public class URoTcpStreamLink extends URoIoStreamLink<Socket> {

    public URoTcpStreamLink(@NonNull URoLinkModel model, @NonNull URoTcpStream source) throws Exception {
        super(model, source);
    }

}
