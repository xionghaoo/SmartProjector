package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbParametersSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 20
 * @FunctionName parameters_set
 * @Description 传感器参数设置
 * @FileName URoUkitSmartParametersSetFormatter.java
 * 
 **/
public class URoUkitSmartParametersSetFormatter extends URoUkitSmartCommandFormatter<PbParametersSet.ParametersSetResponse> {

    private URoUkitSmartParametersSetFormatter(){}

    public static final URoUkitSmartParametersSetFormatter INSTANCE = new URoUkitSmartParametersSetFormatter();

    @Override
    public URoResponse<PbParametersSet.ParametersSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbParametersSet.ParametersSetResponse data;
        try {
            PbParametersSet.ParametersSetResponse response = PbParametersSet.ParametersSetResponse.parseFrom(bizData);
            // TODO check the response
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbParametersSet.ParametersSetRequest.Builder builder = PbParametersSet.ParametersSetRequest.newBuilder();
        /* TODO set request params to pb
        builder.setPath(request.getParameter("path", ""));
        */
        return builder.build().toByteArray();
    }

}
