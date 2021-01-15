package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbTypeGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 17
 * @FunctionName type_get
 * @Description 获取传感器类型
 * @FileName URoUkitSmartTypeGetFormatter.java
 * 
 **/
public class URoUkitSmartTypeGetFormatter extends URoUkitSmartCommandFormatter<PbTypeGet.TypeGetResponse> {

    private URoUkitSmartTypeGetFormatter(){}

    public static final URoUkitSmartTypeGetFormatter INSTANCE = new URoUkitSmartTypeGetFormatter();

    @Override
    public URoResponse<PbTypeGet.TypeGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbTypeGet.TypeGetResponse data;
        try {
            PbTypeGet.TypeGetResponse response = PbTypeGet.TypeGetResponse.parseFrom(bizData);
            // TODO check the response
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbTypeGet.TypeGetRequest.Builder builder = PbTypeGet.TypeGetRequest.newBuilder();
        /* TODO set request params to pb
        builder.setPath(request.getParameter("path", ""));
        */
        return builder.build().toByteArray();
    }

}
