package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevWifiListGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/09/19
 * @CmdId 2510
 * @FunctionName intelligent_dev_wifi_list_get
 * @Description 获取WIIF网络列表
 * @FileName URoUkitSmartIntelligentDevWifiListGetFormatter.java
 * 
 **/
 public class URoUkitSmartIntelligentDevWifiListGetFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevWifiListGet.IntelligentDevWifiListGetResponse> {

    private URoUkitSmartIntelligentDevWifiListGetFormatter(){}

    public static final URoUkitSmartIntelligentDevWifiListGetFormatter INSTANCE = new URoUkitSmartIntelligentDevWifiListGetFormatter();

    @Override
    public URoResponse<PbIntelligentDevWifiListGet.IntelligentDevWifiListGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevWifiListGet.IntelligentDevWifiListGetResponse data;
        try {
            PbIntelligentDevWifiListGet.IntelligentDevWifiListGetResponse response = PbIntelligentDevWifiListGet.IntelligentDevWifiListGetResponse.parseFrom(bizData);
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
