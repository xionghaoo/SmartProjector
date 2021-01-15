package com.ubtedu.base.net.socket.udp.client.bean;

import androidx.annotation.IntDef;

import com.ubtedu.base.net.socket.tcp.client.bean.TargetInfo;
import com.ubtedu.base.net.socket.tcp.client.bean.TcpMsg;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 */
public class UdpMsg extends TcpMsg {

    /**
     * 单播或广播
     */
    public static final int BROADCAST_TYPE_NORMAL = 1;
    /**
     * 组播
     */
    public static final int BROADCAST_TYPE_MULTICAST = 2;


    @BroadcastType
    private int mBroadcastType = BROADCAST_TYPE_NORMAL;

    public UdpMsg(byte[] data, TargetInfo target, MsgType type) {
        super(data, target, type);
    }

    public UdpMsg(String data, TargetInfo target, MsgType type) {
        super(data, target, type);
    }

    public UdpMsg(int id) {
        super(id);
    }

    public int getBroadcastType() {
        return mBroadcastType;
    }

    public void setBroadcastType(int broadcastType) {
        mBroadcastType = broadcastType;
    }

    @Retention(SOURCE)
    @IntDef({
            BROADCAST_TYPE_NORMAL, BROADCAST_TYPE_MULTICAST
    })
    public @interface BroadcastType {
    }

}
