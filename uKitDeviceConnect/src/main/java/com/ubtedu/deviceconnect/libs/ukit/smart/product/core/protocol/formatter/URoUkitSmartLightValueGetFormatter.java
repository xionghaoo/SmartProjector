package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLightValueGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1000
 * @FunctionName light_value_get
 * @Description 回读lux值
 * @FileName URoUkitSmartLightValueGetFormatter.java
 * 
 **/
public class URoUkitSmartLightValueGetFormatter extends URoUkitSmartCommandFormatter<PbLightValueGet.LightValueGetResponse> {

    private URoUkitSmartLightValueGetFormatter(){}

    public static final URoUkitSmartLightValueGetFormatter INSTANCE = new URoUkitSmartLightValueGetFormatter();

    @Override
    public URoResponse<PbLightValueGet.LightValueGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbLightValueGet.LightValueGetResponse data;
        try {
            PbLightValueGet.LightValueGetResponse response = PbLightValueGet.LightValueGetResponse.parseFrom(bizData);
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
