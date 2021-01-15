package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevWifiSsidSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/09/19
 * @CmdId 2509
 * @FunctionName intelligent_dev_wifi_ssid_set
 * @Description 设置WIFI网络连接
 * @FileName URoUkitSmartIntelligentDevWifiSsidSetFormatter.java
 * 
 **/
 public class URoUkitSmartIntelligentDevWifiSsidSetFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevWifiSsidSet.IntelligentDevWifiSsidSetResponse> {

    private URoUkitSmartIntelligentDevWifiSsidSetFormatter(){}

    public static final URoUkitSmartIntelligentDevWifiSsidSetFormatter INSTANCE = new URoUkitSmartIntelligentDevWifiSsidSetFormatter();

    @Override
    public URoResponse<PbIntelligentDevWifiSsidSet.IntelligentDevWifiSsidSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevWifiSsidSet.IntelligentDevWifiSsidSetResponse data;
        try {
            PbIntelligentDevWifiSsidSet.IntelligentDevWifiSsidSetResponse response = PbIntelligentDevWifiSsidSet.IntelligentDevWifiSsidSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbIntelligentDevWifiSsidSet.IntelligentDevWifiSsidSetRequest.Builder builder = PbIntelligentDevWifiSsidSet.IntelligentDevWifiSsidSetRequest.newBuilder();
        builder.setSsid(request.getParameter("ssid", ""));
        builder.setPassword(request.getParameter("password", ""));
        return builder.build().toByteArray();
    }

}
