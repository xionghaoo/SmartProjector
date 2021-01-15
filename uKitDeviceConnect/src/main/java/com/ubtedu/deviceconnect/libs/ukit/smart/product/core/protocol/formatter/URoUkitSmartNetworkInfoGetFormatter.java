package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbNetworkInfoGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/07/01
 * @CmdId 305
 * @FunctionName network_info_get
 * @Description 获取网络连接信息
 * @FileName URoUkitSmartNetworkInfoGetFormatter.java
 * 
 **/
 public class URoUkitSmartNetworkInfoGetFormatter extends URoUkitSmartCommandFormatter<PbNetworkInfoGet.NetworkInfoGetResponse> {

    private URoUkitSmartNetworkInfoGetFormatter(){}

    public static final URoUkitSmartNetworkInfoGetFormatter INSTANCE = new URoUkitSmartNetworkInfoGetFormatter();

    @Override
    public URoResponse<PbNetworkInfoGet.NetworkInfoGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbNetworkInfoGet.NetworkInfoGetResponse data;
        try {
            PbNetworkInfoGet.NetworkInfoGetResponse response = PbNetworkInfoGet.NetworkInfoGetResponse.parseFrom(bizData);
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
