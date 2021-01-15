package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLightCoefficientGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1003
 * @FunctionName light_coefficient_get
 * @Description 回读标定系数
 * @FileName URoUkitSmartLightCoefficientGetFormatter.java
 * 
 **/
public class URoUkitSmartLightCoefficientGetFormatter extends URoUkitSmartCommandFormatter<PbLightCoefficientGet.LightCoefficientGetResponse> {

    private URoUkitSmartLightCoefficientGetFormatter(){}

    public static final URoUkitSmartLightCoefficientGetFormatter INSTANCE = new URoUkitSmartLightCoefficientGetFormatter();

    @Override
    public URoResponse<PbLightCoefficientGet.LightCoefficientGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbLightCoefficientGet.LightCoefficientGetResponse data;
        try {
            PbLightCoefficientGet.LightCoefficientGetResponse response = PbLightCoefficientGet.LightCoefficientGetResponse.parseFrom(bizData);
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
