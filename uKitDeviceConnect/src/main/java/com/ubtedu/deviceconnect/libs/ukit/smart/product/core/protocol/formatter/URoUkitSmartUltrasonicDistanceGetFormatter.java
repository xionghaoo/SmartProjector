package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbUltrasonicDistanceGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId: 1000
 * @FunctionName：ultrasonic_distance_get
 * @Description：读取超声距离值
 * @FileName: URoUkitSmartUltrasonicDistanceGetFormatter.java
 * 
 **/
public class URoUkitSmartUltrasonicDistanceGetFormatter extends URoUkitSmartCommandFormatter<PbUltrasonicDistanceGet.UltrasonicDistanceGetResponse> {

    private URoUkitSmartUltrasonicDistanceGetFormatter(){}

    public static final URoUkitSmartUltrasonicDistanceGetFormatter INSTANCE = new URoUkitSmartUltrasonicDistanceGetFormatter();

    @Override
    public URoResponse<PbUltrasonicDistanceGet.UltrasonicDistanceGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbUltrasonicDistanceGet.UltrasonicDistanceGetResponse data;
        try {
            PbUltrasonicDistanceGet.UltrasonicDistanceGetResponse response = PbUltrasonicDistanceGet.UltrasonicDistanceGetResponse.parseFrom(bizData);
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
