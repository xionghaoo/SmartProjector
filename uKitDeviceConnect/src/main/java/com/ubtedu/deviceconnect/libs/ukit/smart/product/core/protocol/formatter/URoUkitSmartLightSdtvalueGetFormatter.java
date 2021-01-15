package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLightSdtvalueGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1002
 * @FunctionName light_sdtvalue_get
 * @Description 回读照度标定lux值
 * @FileName URoUkitSmartLightSdtvalueGetFormatter.java
 * 
 **/
public class URoUkitSmartLightSdtvalueGetFormatter extends URoUkitSmartCommandFormatter<PbLightSdtvalueGet.LightSdtvalueGetResponse> {

    private URoUkitSmartLightSdtvalueGetFormatter(){}

    public static final URoUkitSmartLightSdtvalueGetFormatter INSTANCE = new URoUkitSmartLightSdtvalueGetFormatter();

    @Override
    public URoResponse<PbLightSdtvalueGet.LightSdtvalueGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbLightSdtvalueGet.LightSdtvalueGetResponse data;
        try {
            PbLightSdtvalueGet.LightSdtvalueGetResponse response = PbLightSdtvalueGet.LightSdtvalueGetResponse.parseFrom(bizData);
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
