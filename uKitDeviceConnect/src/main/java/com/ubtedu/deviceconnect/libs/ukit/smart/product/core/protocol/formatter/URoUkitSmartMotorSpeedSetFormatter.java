package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbMotorSpeedSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1000
 * @FunctionName motor_speed_set
 * @Description 设置转动速度
 * @FileName URoUkitSmartMotorSpeedSetFormatter.java
 * 
 **/
public class URoUkitSmartMotorSpeedSetFormatter extends URoUkitSmartCommandFormatter<PbMotorSpeedSet.MotorSpeedSetResponse> {

    private URoUkitSmartMotorSpeedSetFormatter(){}

    public static final URoUkitSmartMotorSpeedSetFormatter INSTANCE = new URoUkitSmartMotorSpeedSetFormatter();

    @Override
    public URoResponse<PbMotorSpeedSet.MotorSpeedSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbMotorSpeedSet.MotorSpeedSetResponse data;
        try {
            PbMotorSpeedSet.MotorSpeedSetResponse response = PbMotorSpeedSet.MotorSpeedSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbMotorSpeedSet.MotorSpeedSetRequest.Builder builder = PbMotorSpeedSet.MotorSpeedSetRequest.newBuilder();
        builder.setSpeed(request.getParameter("speed", 0));
        builder.setTime(request.getParameter("time", 0xFFFFFFFF));
        return builder.build().toByteArray();
    }

}
