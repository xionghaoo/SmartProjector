package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbReset;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 7
 * @FunctionName reset
 * @Description 重启
 * @FileName URoUkitSmartResetFormatter.java
 * 
 **/
public class URoUkitSmartResetFormatter extends URoUkitSmartCommandFormatter<PbReset.ResetResponse> {

    private URoUkitSmartResetFormatter(){}

    public static final URoUkitSmartResetFormatter INSTANCE = new URoUkitSmartResetFormatter();

    @Override
    public URoResponse<PbReset.ResetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbReset.ResetResponse data;
        try {
            PbReset.ResetResponse response = PbReset.ResetResponse.parseFrom(bizData);
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
