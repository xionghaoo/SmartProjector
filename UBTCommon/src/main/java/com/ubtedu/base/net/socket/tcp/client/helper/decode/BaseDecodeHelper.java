package com.ubtedu.base.net.socket.tcp.client.helper.decode;

import com.ubtedu.base.net.socket.tcp.client.TcpConnConfig;
import com.ubtedu.base.net.socket.tcp.client.bean.TargetInfo;

public class BaseDecodeHelper implements AbsDecodeHelper {
    @Override
    public byte[][] execute(byte[] data, TargetInfo targetInfo, TcpConnConfig tcpConnConfig) {
        return new byte[][]{data};
    }
}
