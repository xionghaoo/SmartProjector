package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbParametersGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 21
 * @FunctionName parameters_get
 * @Description 传感器参数读取
 * @FileName URoUkitSmartParametersGetFormatter.java
 * 
 **/
public class URoUkitSmartParametersGetFormatter extends URoUkitSmartCommandFormatter<PbParametersGet.ParametersGetResponse> {

    private URoUkitSmartParametersGetFormatter(){}

    public static final URoUkitSmartParametersGetFormatter INSTANCE = new URoUkitSmartParametersGetFormatter();

    @Override
    public URoResponse<PbParametersGet.ParametersGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbParametersGet.ParametersGetResponse data;
        try {
            PbParametersGet.ParametersGetResponse response = PbParametersGet.ParametersGetResponse.parseFrom(bizData);
            // TODO check the response
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbParametersGet.ParametersGetRequest.Builder builder = PbParametersGet.ParametersGetRequest.newBuilder();
        /* TODO set request params to pb
        builder.setPath(request.getParameter("path", ""));
        */
        return builder.build().toByteArray();
    }

}
