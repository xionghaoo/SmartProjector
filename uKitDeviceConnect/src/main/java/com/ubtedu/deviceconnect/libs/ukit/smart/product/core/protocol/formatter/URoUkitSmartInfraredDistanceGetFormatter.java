package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbInfraredDistanceGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1001
 * @FunctionName infrared_distance_get
 * @Description 读取红外距离值
 * @FileName URoUkitSmartInfraredDistanceGetFormatter.java
 * 
 **/
public class URoUkitSmartInfraredDistanceGetFormatter extends URoUkitSmartCommandFormatter<PbInfraredDistanceGet.InfraredDistanceGetResponse> {

    private URoUkitSmartInfraredDistanceGetFormatter(){}

    public static final URoUkitSmartInfraredDistanceGetFormatter INSTANCE = new URoUkitSmartInfraredDistanceGetFormatter();

    @Override
    public URoResponse<PbInfraredDistanceGet.InfraredDistanceGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbInfraredDistanceGet.InfraredDistanceGetResponse data;
        try {
            PbInfraredDistanceGet.InfraredDistanceGetResponse response = PbInfraredDistanceGet.InfraredDistanceGetResponse.parseFrom(bizData);
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
