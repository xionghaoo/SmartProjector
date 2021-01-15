package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevWifiConnectSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/09/27
 * @CmdId 2511
 * @FunctionName intelligent_dev_wifi_connect_set
 * @Description WIFI连接设置
 * @FileName URoUkitSmartIntelligentDevWifiConnectSetFormatter.java
 * 
 **/
 public class URoUkitSmartIntelligentDevWifiConnectSetFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevWifiConnectSet.IntelligentDevWifiConnectSetResponse> {

    private URoUkitSmartIntelligentDevWifiConnectSetFormatter(){}

    public static final URoUkitSmartIntelligentDevWifiConnectSetFormatter INSTANCE = new URoUkitSmartIntelligentDevWifiConnectSetFormatter();

    @Override
    public URoResponse<PbIntelligentDevWifiConnectSet.IntelligentDevWifiConnectSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevWifiConnectSet.IntelligentDevWifiConnectSetResponse data;
        try {
            PbIntelligentDevWifiConnectSet.IntelligentDevWifiConnectSetResponse response = PbIntelligentDevWifiConnectSet.IntelligentDevWifiConnectSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbIntelligentDevWifiConnectSet.IntelligentDevWifiConnectSetRequest.Builder builder = PbIntelligentDevWifiConnectSet.IntelligentDevWifiConnectSetRequest.newBuilder();
        builder.setStatus(request.getParameter("status", false) ? 1 : 0);
        return builder.build().toByteArray();
    }

}
