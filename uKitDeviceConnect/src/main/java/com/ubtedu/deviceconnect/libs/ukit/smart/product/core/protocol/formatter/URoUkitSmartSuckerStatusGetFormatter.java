package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbSuckerStatusGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/12/20
 * @CmdId 1001
 * @FunctionName sucker_status_get
 * @Description 获取吸盘状态
 * @FileName URoUkitSmartSuckerStatusGetFormatter.java
 * 
 **/
 public class URoUkitSmartSuckerStatusGetFormatter extends URoUkitSmartCommandFormatter<PbSuckerStatusGet.SuckerStatusGetResponse> {

    private URoUkitSmartSuckerStatusGetFormatter(){}

    public static final URoUkitSmartSuckerStatusGetFormatter INSTANCE = new URoUkitSmartSuckerStatusGetFormatter();

    @Override
    public URoResponse<PbSuckerStatusGet.SuckerStatusGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbSuckerStatusGet.SuckerStatusGetResponse data;
        try {
            PbSuckerStatusGet.SuckerStatusGetResponse response = PbSuckerStatusGet.SuckerStatusGetResponse.parseFrom(bizData);
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
