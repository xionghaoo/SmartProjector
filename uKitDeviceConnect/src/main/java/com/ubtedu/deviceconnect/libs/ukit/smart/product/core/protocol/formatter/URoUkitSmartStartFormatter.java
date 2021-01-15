package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbStart;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 9
 * @FunctionName start
 * @Description 启动传感器
 * @FileName URoUkitSmartStartFormatter.java
 * 
 **/
public class URoUkitSmartStartFormatter extends URoUkitSmartCommandFormatter<PbStart.StartResponse> {

    private URoUkitSmartStartFormatter(){}

    public static final URoUkitSmartStartFormatter INSTANCE = new URoUkitSmartStartFormatter();

    @Override
    public URoResponse<PbStart.StartResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbStart.StartResponse data;
        try {
            PbStart.StartResponse response = PbStart.StartResponse.parseFrom(bizData);
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
