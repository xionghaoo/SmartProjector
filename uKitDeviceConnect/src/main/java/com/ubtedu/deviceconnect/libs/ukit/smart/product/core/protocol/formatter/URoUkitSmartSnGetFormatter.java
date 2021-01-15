package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbSnGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1
 * @FunctionName sn_get
 * @Description 获取产品序列号
 * @FileName URoUkitSmartSnGetFormatter.java
 * 
 **/
public class URoUkitSmartSnGetFormatter extends URoUkitSmartCommandFormatter<PbSnGet.SnGetResponse> {

    private URoUkitSmartSnGetFormatter(){}

    public static final URoUkitSmartSnGetFormatter INSTANCE = new URoUkitSmartSnGetFormatter();

    @Override
    public URoResponse<PbSnGet.SnGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbSnGet.SnGetResponse data;
        try {
            PbSnGet.SnGetResponse response = PbSnGet.SnGetResponse.parseFrom(bizData);
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
