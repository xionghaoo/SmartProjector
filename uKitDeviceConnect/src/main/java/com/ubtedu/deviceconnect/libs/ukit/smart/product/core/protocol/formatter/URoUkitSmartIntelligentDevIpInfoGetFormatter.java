package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevIpInfoGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/12/13
 * @CmdId 2524
 * @FunctionName intelligent_dev_ip_info_get
 * @Description 获取IP信息
 * @FileName URoUkitSmartIntelligentDevIpInfoGetFormatter.java
 * 
 **/
 public class URoUkitSmartIntelligentDevIpInfoGetFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevIpInfoGet.IntelligentDevIpInfoGetResponse> {

    private URoUkitSmartIntelligentDevIpInfoGetFormatter(){}

    public static final URoUkitSmartIntelligentDevIpInfoGetFormatter INSTANCE = new URoUkitSmartIntelligentDevIpInfoGetFormatter();

    @Override
    public URoResponse<PbIntelligentDevIpInfoGet.IntelligentDevIpInfoGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevIpInfoGet.IntelligentDevIpInfoGetResponse data;
        try {
            PbIntelligentDevIpInfoGet.IntelligentDevIpInfoGetResponse response = PbIntelligentDevIpInfoGet.IntelligentDevIpInfoGetResponse.parseFrom(bizData);
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
