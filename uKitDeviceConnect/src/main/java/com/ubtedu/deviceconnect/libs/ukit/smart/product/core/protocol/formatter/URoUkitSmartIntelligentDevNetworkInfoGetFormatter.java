package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevNetworkInfoGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/10/14
 * @CmdId 2518
 * @FunctionName intelligent_dev_network_info_get
 * @Description 获取网络连接信息
 * @FileName URoUkitSmartIntelligentDevNetworkInfoGetFormatter.java
 * 
 **/
 public class URoUkitSmartIntelligentDevNetworkInfoGetFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevNetworkInfoGet.IntelligentDevNetworkInfoGetResponse> {

    private URoUkitSmartIntelligentDevNetworkInfoGetFormatter(){}

    public static final URoUkitSmartIntelligentDevNetworkInfoGetFormatter INSTANCE = new URoUkitSmartIntelligentDevNetworkInfoGetFormatter();

    @Override
    public URoResponse<PbIntelligentDevNetworkInfoGet.IntelligentDevNetworkInfoGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevNetworkInfoGet.IntelligentDevNetworkInfoGetResponse data;
        try {
            PbIntelligentDevNetworkInfoGet.IntelligentDevNetworkInfoGetResponse response = PbIntelligentDevNetworkInfoGet.IntelligentDevNetworkInfoGetResponse.parseFrom(bizData);
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
