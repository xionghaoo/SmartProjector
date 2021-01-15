package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIpInfoGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/07/01
 * @CmdId 308
 * @FunctionName ip_info_get
 * @Description 获取IP信息
 * @FileName URoUkitSmartIpInfoGetFormatter.java
 * 
 **/
 public class URoUkitSmartIpInfoGetFormatter extends URoUkitSmartCommandFormatter<PbIpInfoGet.IpInfoGetResponse> {

    private URoUkitSmartIpInfoGetFormatter(){}

    public static final URoUkitSmartIpInfoGetFormatter INSTANCE = new URoUkitSmartIpInfoGetFormatter();

    @Override
    public URoResponse<PbIpInfoGet.IpInfoGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIpInfoGet.IpInfoGetResponse data;
        try {
            PbIpInfoGet.IpInfoGetResponse response = PbIpInfoGet.IpInfoGetResponse.parseFrom(bizData);
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
