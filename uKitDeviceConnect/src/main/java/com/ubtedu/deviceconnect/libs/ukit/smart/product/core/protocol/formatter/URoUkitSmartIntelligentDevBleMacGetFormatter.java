package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevBleMacGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/12/13
 * @CmdId 2522
 * @FunctionName intelligent_dev_ble_mac_get
 * @Description 获取蓝牙的mac信息
 * @FileName URoUkitSmartIntelligentDevBleMacGetFormatter.java
 * 
 **/
 public class URoUkitSmartIntelligentDevBleMacGetFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevBleMacGet.IntelligentDevBleMacGetResponse> {

    private URoUkitSmartIntelligentDevBleMacGetFormatter(){}

    public static final URoUkitSmartIntelligentDevBleMacGetFormatter INSTANCE = new URoUkitSmartIntelligentDevBleMacGetFormatter();

    @Override
    public URoResponse<PbIntelligentDevBleMacGet.IntelligentDevBleMacGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevBleMacGet.IntelligentDevBleMacGetResponse data;
        try {
            PbIntelligentDevBleMacGet.IntelligentDevBleMacGetResponse response = PbIntelligentDevBleMacGet.IntelligentDevBleMacGetResponse.parseFrom(bizData);
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
