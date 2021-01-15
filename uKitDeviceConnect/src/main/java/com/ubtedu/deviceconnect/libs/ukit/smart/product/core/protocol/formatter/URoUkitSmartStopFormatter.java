package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbStop;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId: 10
 * @FunctionName：stop
 * @Description：停止传感器
 * @FileName: URoUkitSmartStopFormatter.java
 * 
 **/
public class URoUkitSmartStopFormatter extends URoUkitSmartCommandFormatter<PbStop.StopResponse> {

    private URoUkitSmartStopFormatter(){}

    public static final URoUkitSmartStopFormatter INSTANCE = new URoUkitSmartStopFormatter();

    @Override
    public URoResponse<PbStop.StopResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbStop.StopResponse data;
        try {
            PbStop.StopResponse response = PbStop.StopResponse.parseFrom(bizData);
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
