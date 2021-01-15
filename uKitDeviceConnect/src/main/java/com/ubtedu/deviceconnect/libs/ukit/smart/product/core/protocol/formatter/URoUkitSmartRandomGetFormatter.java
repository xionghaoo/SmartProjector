package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbRandomGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 18
 * @FunctionName random_get
 * @Description 获取随机码
 * @FileName URoUkitSmartRandomGetFormatter.java
 * 
 **/
public class URoUkitSmartRandomGetFormatter extends URoUkitSmartCommandFormatter<PbRandomGet.RandomGetResponse> {

    private URoUkitSmartRandomGetFormatter(){}

    public static final URoUkitSmartRandomGetFormatter INSTANCE = new URoUkitSmartRandomGetFormatter();

    @Override
    public URoResponse<PbRandomGet.RandomGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbRandomGet.RandomGetResponse data;
        try {
            PbRandomGet.RandomGetResponse response = PbRandomGet.RandomGetResponse.parseFrom(bizData);
            // TODO check the response
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbRandomGet.RandomGetRequest.Builder builder = PbRandomGet.RandomGetRequest.newBuilder();
        /* TODO set request params to pb
        builder.setPath(request.getParameter("path", ""));
        */
        return builder.build().toByteArray();
    }

}
