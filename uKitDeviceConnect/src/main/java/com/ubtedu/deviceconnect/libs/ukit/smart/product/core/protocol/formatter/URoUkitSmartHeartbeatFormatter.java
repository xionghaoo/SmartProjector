package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbHeartbeat;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 8
 * @FunctionName heartbeat
 * @Description 心跳
 * @FileName URoUkitSmartHeartbeatFormatter.java
 * 
 **/
public class URoUkitSmartHeartbeatFormatter extends URoUkitSmartCommandFormatter<PbHeartbeat.HeartbeatResponse> {

    private URoUkitSmartHeartbeatFormatter(){}

    public static final URoUkitSmartHeartbeatFormatter INSTANCE = new URoUkitSmartHeartbeatFormatter();

    @Override
    public URoResponse<PbHeartbeat.HeartbeatResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbHeartbeat.HeartbeatResponse data;
        try {
            PbHeartbeat.HeartbeatResponse response = PbHeartbeat.HeartbeatResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        return EMPTY_REQUEST_DATA;
    }

}
