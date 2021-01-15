package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbMotorPwmSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1004
 * @FunctionName motor_pwm_set
 * @Description 马达pwm控制
 * @FileName URoUkitSmartMotorPwmSetFormatter.java
 * 
 **/
public class URoUkitSmartMotorPwmSetFormatter extends URoUkitSmartCommandFormatter<PbMotorPwmSet.MotorPwmSetResponse> {

    private URoUkitSmartMotorPwmSetFormatter(){}

    public static final URoUkitSmartMotorPwmSetFormatter INSTANCE = new URoUkitSmartMotorPwmSetFormatter();

    @Override
    public URoResponse<PbMotorPwmSet.MotorPwmSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbMotorPwmSet.MotorPwmSetResponse data;
        try {
            PbMotorPwmSet.MotorPwmSetResponse response = PbMotorPwmSet.MotorPwmSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbMotorPwmSet.MotorPwmSetRequest.Builder builder = PbMotorPwmSet.MotorPwmSetRequest.newBuilder();
        builder.setPwm(request.getParameter("pwm", 0));
        return builder.build().toByteArray();
    }

}
