package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbWifiInfoGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/07/01
 * @CmdId 303
 * @FunctionName wifi_info_get
 * @Description 获取WIFI连接信息
 * @FileName URoUkitSmartWifiInfoGetFormatter.java
 * 
 **/
 public class URoUkitSmartWifiInfoGetFormatter extends URoUkitSmartCommandFormatter<PbWifiInfoGet.WifiInfoGetResponse> {

    private URoUkitSmartWifiInfoGetFormatter(){}

    public static final URoUkitSmartWifiInfoGetFormatter INSTANCE = new URoUkitSmartWifiInfoGetFormatter();

    @Override
    public URoResponse<PbWifiInfoGet.WifiInfoGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbWifiInfoGet.WifiInfoGetResponse data;
        try {
            PbWifiInfoGet.WifiInfoGetResponse response = PbWifiInfoGet.WifiInfoGetResponse.parseFrom(bizData);
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
