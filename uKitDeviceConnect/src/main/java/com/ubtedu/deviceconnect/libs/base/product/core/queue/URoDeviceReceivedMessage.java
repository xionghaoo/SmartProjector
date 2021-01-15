package com.ubtedu.deviceconnect.libs.base.product.core.queue;

/**
 * @Author naOKi
 * @Date 2019/06/17
 **/
public class URoDeviceReceivedMessage {

    public String identity;
    public URoCommand cmd;
    public URoResponse response;
    public byte[] bizData;
    public byte[] rawResponse;
    public long receiveTime;
    public int errorCode;
    private Object extraData;

    public URoDeviceReceivedMessage(URoCommand cmd, int errorCode, String identity, byte[] bizData, byte[] rawResponse, long receiveTime) {
        this.cmd = cmd;
        this.errorCode = errorCode;
        this.identity = identity;
        this.bizData = bizData;
        this.rawResponse = rawResponse;
        this.receiveTime = receiveTime;
    }

    public URoDeviceReceivedMessage(URoCommand cmd, String identity, byte[] bizData, byte[] rawResponse, long receiveTime) {
        this(cmd, 0, identity, bizData, rawResponse, receiveTime);
    }

    public void setExtraData(Object extraData) {
        this.extraData = extraData;
    }

    public Object getExtraData() {
        return extraData;
    }

}
