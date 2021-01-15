package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbServoWheelSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1003
 * @FunctionName servo_wheel_set
 * @Description 恒速轮模式
 * @FileName URoUkitSmartServoWheelSetFormatter.java
 * 
 **/
public class URoUkitSmartServoWheelSetFormatter extends URoUkitSmartCommandFormatter<PbServoWheelSet.ServoWheelSetResponse> {

    private URoUkitSmartServoWheelSetFormatter(){}

    public static final URoUkitSmartServoWheelSetFormatter INSTANCE = new URoUkitSmartServoWheelSetFormatter();

    @Override
    public URoResponse<PbServoWheelSet.ServoWheelSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbServoWheelSet.ServoWheelSetResponse data;
        try {
            PbServoWheelSet.ServoWheelSetResponse response = PbServoWheelSet.ServoWheelSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbServoWheelSet.ServoWheelSetRequest.Builder builder = PbServoWheelSet.ServoWheelSetRequest.newBuilder();
        builder.setRotatingSpeed(request.getParameter("rotating_speed", 0));
        return builder.build().toByteArray();
    }

}
