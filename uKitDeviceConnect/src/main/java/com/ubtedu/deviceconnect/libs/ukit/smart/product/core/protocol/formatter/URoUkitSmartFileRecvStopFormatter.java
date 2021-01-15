package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbFileRecvStop;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 109
 * @FunctionName file_recv_stop
 * @Description 强制结束回传文件
 * @FileName URoUkitSmartFileRecvStopFormatter.java
 * 
 **/
public class URoUkitSmartFileRecvStopFormatter extends URoUkitSmartCommandFormatter<PbFileRecvStop.FileRecvStopResponse> {

    private URoUkitSmartFileRecvStopFormatter(){}

    public static final URoUkitSmartFileRecvStopFormatter INSTANCE = new URoUkitSmartFileRecvStopFormatter();

    @Override
    public URoResponse<PbFileRecvStop.FileRecvStopResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbFileRecvStop.FileRecvStopResponse data;
        try {
            PbFileRecvStop.FileRecvStopResponse response = PbFileRecvStop.FileRecvStopResponse.parseFrom(bizData);
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
