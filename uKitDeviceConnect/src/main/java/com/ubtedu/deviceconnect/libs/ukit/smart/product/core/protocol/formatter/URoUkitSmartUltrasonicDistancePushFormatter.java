package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbUltrasonicDistancePush;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/10/24
 * @CmdId 1002
 * @FunctionName ultrasonic_distance_push
 * @Description 读取超声距离值
 * @FileName URoUkitSmartUltrasonicDistancePushFormatter.java
 * 
 **/
 public class URoUkitSmartUltrasonicDistancePushFormatter extends URoUkitSmartCommandFormatter<PbUltrasonicDistancePush.UltrasonicDistancePushResponse> {

    private URoUkitSmartUltrasonicDistancePushFormatter(){}

    public static final URoUkitSmartUltrasonicDistancePushFormatter INSTANCE = new URoUkitSmartUltrasonicDistancePushFormatter();

    @Override
    public URoResponse<PbUltrasonicDistancePush.UltrasonicDistancePushResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbUltrasonicDistancePush.UltrasonicDistancePushResponse data;
        try {
            PbUltrasonicDistancePush.UltrasonicDistancePushResponse response = PbUltrasonicDistancePush.UltrasonicDistancePushResponse.parseFrom(bizData);
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
