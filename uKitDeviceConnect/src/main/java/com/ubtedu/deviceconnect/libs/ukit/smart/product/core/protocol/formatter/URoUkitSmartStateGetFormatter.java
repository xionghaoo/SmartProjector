package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbStateGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId: 11
 * @FunctionName：state_get
 * @Description：获取当前工作状态
 * @FileName: URoUkitSmartStateGetFormatter.java
 * 
 **/
public class URoUkitSmartStateGetFormatter extends URoUkitSmartCommandFormatter<PbStateGet.StateGetResponse> {

    private URoUkitSmartStateGetFormatter(){}

    public static final URoUkitSmartStateGetFormatter INSTANCE = new URoUkitSmartStateGetFormatter();

    @Override
    public URoResponse<PbStateGet.StateGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbStateGet.StateGetResponse data;
        try {
            PbStateGet.StateGetResponse response = PbStateGet.StateGetResponse.parseFrom(bizData);
            // TODO check the response
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbStateGet.StateGetRequest.Builder builder = PbStateGet.StateGetRequest.newBuilder();
        /* TODO set request params to pb
        builder.setPath(request.getParameter("path", ""));
        */
        return builder.build().toByteArray();
    }

}
