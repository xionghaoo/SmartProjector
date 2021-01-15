package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbBleMacGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/07/01
 * @CmdId 352
 * @FunctionName ble_mac_get
 * @Description 获取蓝牙的mac信息
 * @FileName URoUkitSmartBleMacGetFormatter.java
 * 
 **/
 public class URoUkitSmartBleMacGetFormatter extends URoUkitSmartCommandFormatter<PbBleMacGet.BleMacGetResponse> {

    private URoUkitSmartBleMacGetFormatter(){}

    public static final URoUkitSmartBleMacGetFormatter INSTANCE = new URoUkitSmartBleMacGetFormatter();

    @Override
    public URoResponse<PbBleMacGet.BleMacGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbBleMacGet.BleMacGetResponse data;
        try {
            PbBleMacGet.BleMacGetResponse response = PbBleMacGet.BleMacGetResponse.parseFrom(bizData);
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
