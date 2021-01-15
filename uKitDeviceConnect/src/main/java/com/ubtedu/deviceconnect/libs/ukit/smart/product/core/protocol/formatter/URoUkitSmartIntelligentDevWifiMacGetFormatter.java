package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevWifiMacGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/12/13
 * @CmdId 2523
 * @FunctionName intelligent_dev_wifi_mac_get
 * @Description 获取WIFI的mac信息
 * @FileName URoUkitSmartIntelligentDevWifiMacGetFormatter.java
 * 
 **/
 public class URoUkitSmartIntelligentDevWifiMacGetFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevWifiMacGet.IntelligentDevWifiMacGetResponse> {

    private URoUkitSmartIntelligentDevWifiMacGetFormatter(){}

    public static final URoUkitSmartIntelligentDevWifiMacGetFormatter INSTANCE = new URoUkitSmartIntelligentDevWifiMacGetFormatter();

    @Override
    public URoResponse<PbIntelligentDevWifiMacGet.IntelligentDevWifiMacGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevWifiMacGet.IntelligentDevWifiMacGetResponse data;
        try {
            PbIntelligentDevWifiMacGet.IntelligentDevWifiMacGetResponse response = PbIntelligentDevWifiMacGet.IntelligentDevWifiMacGetResponse.parseFrom(bizData);
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
