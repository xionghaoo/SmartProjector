package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbSnSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 2
 * @FunctionName sn_set
 * @Description 设置产品序列号
 * @FileName URoUkitSmartSnSetFormatter.java
 * 
 **/
public class URoUkitSmartSnSetFormatter extends URoUkitSmartCommandFormatter<PbSnSet.SnSetResponse> {

    private URoUkitSmartSnSetFormatter(){}

    public static final URoUkitSmartSnSetFormatter INSTANCE = new URoUkitSmartSnSetFormatter();

    @Override
    public URoResponse<PbSnSet.SnSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbSnSet.SnSetResponse data;
        try {
            PbSnSet.SnSetResponse response = PbSnSet.SnSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbSnSet.SnSetRequest.Builder builder = PbSnSet.SnSetRequest.newBuilder();
        builder.setSn(ByteString.copyFrom(request.getParameter("sn", new byte[0])));
        return builder.build().toByteArray();
    }

}
