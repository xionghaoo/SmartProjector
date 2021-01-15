package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbWifiMacGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/07/01
 * @CmdId 307
 * @FunctionName wifi_mac_get
 * @Description 获取WIFI的mac信息
 * @FileName URoUkitSmartWifiMacGetFormatter.java
 * 
 **/
 public class URoUkitSmartWifiMacGetFormatter extends URoUkitSmartCommandFormatter<PbWifiMacGet.WifiMacGetResponse> {

    private URoUkitSmartWifiMacGetFormatter(){}

    public static final URoUkitSmartWifiMacGetFormatter INSTANCE = new URoUkitSmartWifiMacGetFormatter();

    @Override
    public URoResponse<PbWifiMacGet.WifiMacGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbWifiMacGet.WifiMacGetResponse data;
        try {
            PbWifiMacGet.WifiMacGetResponse response = PbWifiMacGet.WifiMacGetResponse.parseFrom(bizData);
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
