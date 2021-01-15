package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevWifiInfoGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/09/27
 * @CmdId 2512
 * @FunctionName intelligent_dev_wifi_info_get
 * @Description 获取WIIF网络信息
 * @FileName URoUkitSmartIntelligentDevWifiInfoGetFormatter.java
 * 
 **/
 public class URoUkitSmartIntelligentDevWifiInfoGetFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevWifiInfoGet.IntelligentDevWifiInfoGetResponse> {

    private URoUkitSmartIntelligentDevWifiInfoGetFormatter(){}

    public static final URoUkitSmartIntelligentDevWifiInfoGetFormatter INSTANCE = new URoUkitSmartIntelligentDevWifiInfoGetFormatter();

    @Override
    public URoResponse<PbIntelligentDevWifiInfoGet.IntelligentDevWifiInfoGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevWifiInfoGet.IntelligentDevWifiInfoGetResponse data;
        try {
            PbIntelligentDevWifiInfoGet.IntelligentDevWifiInfoGetResponse response = PbIntelligentDevWifiInfoGet.IntelligentDevWifiInfoGetResponse.parseFrom(bizData);
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
