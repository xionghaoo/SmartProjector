package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbWifiSsidSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/07/01
 * @CmdId 300
 * @FunctionName wifi_ssid_set
 * @Description 设置WIFI配置
 * @FileName URoUkitSmartWifiSsidSetFormatter.java
 * 
 **/
 public class URoUkitSmartWifiSsidSetFormatter extends URoUkitSmartCommandFormatter<PbWifiSsidSet.WifiSsidSetResponse> {

    private URoUkitSmartWifiSsidSetFormatter(){}

    public static final URoUkitSmartWifiSsidSetFormatter INSTANCE = new URoUkitSmartWifiSsidSetFormatter();

    @Override
    public URoResponse<PbWifiSsidSet.WifiSsidSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbWifiSsidSet.WifiSsidSetResponse data;
        try {
            PbWifiSsidSet.WifiSsidSetResponse response = PbWifiSsidSet.WifiSsidSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbWifiSsidSet.WifiSsidSetRequest.Builder builder = PbWifiSsidSet.WifiSsidSetRequest.newBuilder();
        builder.setSsid(request.getParameter("ssid", ""));
        builder.setPassword(request.getParameter("password", ""));
        return builder.build().toByteArray();
    }

}
