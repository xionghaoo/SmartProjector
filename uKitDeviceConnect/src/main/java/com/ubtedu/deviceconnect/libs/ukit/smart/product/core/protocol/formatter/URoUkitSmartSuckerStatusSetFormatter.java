package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbSuckerStatusSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/12/20
 * @CmdId 1000
 * @FunctionName sucker_status_set
 * @Description 设置吸盘状态
 * @FileName URoUkitSmartSuckerStatusSetFormatter.java
 * 
 **/
 public class URoUkitSmartSuckerStatusSetFormatter extends URoUkitSmartCommandFormatter<PbSuckerStatusSet.SuckerStatusSetResponse> {

    private URoUkitSmartSuckerStatusSetFormatter(){}

    public static final URoUkitSmartSuckerStatusSetFormatter INSTANCE = new URoUkitSmartSuckerStatusSetFormatter();

    @Override
    public URoResponse<PbSuckerStatusSet.SuckerStatusSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbSuckerStatusSet.SuckerStatusSetResponse data;
        try {
            PbSuckerStatusSet.SuckerStatusSetResponse response = PbSuckerStatusSet.SuckerStatusSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbSuckerStatusSet.SuckerStatusSetRequest.Builder builder = PbSuckerStatusSet.SuckerStatusSetRequest.newBuilder();
        builder.setStatus(request.getParameter("status", false));
        return builder.build().toByteArray();
    }

}
