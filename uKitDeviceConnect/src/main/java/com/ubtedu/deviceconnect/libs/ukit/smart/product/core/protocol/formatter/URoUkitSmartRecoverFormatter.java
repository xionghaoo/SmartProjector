package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbRecover;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 6
 * @FunctionName recover
 * @Description 恢复出厂设置
 * @FileName URoUkitSmartRecoverFormatter.java
 * 
 **/
public class URoUkitSmartRecoverFormatter extends URoUkitSmartCommandFormatter<PbRecover.RecoverResponse> {

    private URoUkitSmartRecoverFormatter(){}

    public static final URoUkitSmartRecoverFormatter INSTANCE = new URoUkitSmartRecoverFormatter();

    @Override
    public URoResponse<PbRecover.RecoverResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbRecover.RecoverResponse data;
        try {
            PbRecover.RecoverResponse response = PbRecover.RecoverResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbRecover.RecoverRequest.Builder builder = PbRecover.RecoverRequest.newBuilder();
        builder.setRecover(request.getParameter("recover", 1));
        return builder.build().toByteArray();
    }

}
