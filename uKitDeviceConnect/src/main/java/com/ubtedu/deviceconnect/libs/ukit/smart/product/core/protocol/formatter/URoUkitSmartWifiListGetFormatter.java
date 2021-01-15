package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbWifiListGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/07/01
 * @CmdId 301
 * @FunctionName wifi_list_get
 * @Description 获取WIIF列表
 * @FileName URoUkitSmartWifiListGetFormatter.java
 * 
 **/
 public class URoUkitSmartWifiListGetFormatter extends URoUkitSmartCommandFormatter<PbWifiListGet.WifiListGetResponse> {

    private URoUkitSmartWifiListGetFormatter(){}

    public static final URoUkitSmartWifiListGetFormatter INSTANCE = new URoUkitSmartWifiListGetFormatter();

    @Override
    public URoResponse<PbWifiListGet.WifiListGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbWifiListGet.WifiListGetResponse data;
        try {
            PbWifiListGet.WifiListGetResponse response = PbWifiListGet.WifiListGetResponse.parseFrom(bizData);
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
