package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbMotorPwmUpperLimitSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1003
 * @FunctionName motor_pwm_upper_limit_set
 * @Description 获取马达pwm的上限值
 * @FileName URoUkitSmartMotorPwmUpperLimitSetFormatter.java
 * 
 **/
public class URoUkitSmartMotorPwmUpperLimitSetFormatter extends URoUkitSmartCommandFormatter<PbMotorPwmUpperLimitSet.MotorPwmUpperLimitSetResponse> {

    private URoUkitSmartMotorPwmUpperLimitSetFormatter(){}

    public static final URoUkitSmartMotorPwmUpperLimitSetFormatter INSTANCE = new URoUkitSmartMotorPwmUpperLimitSetFormatter();

    @Override
    public URoResponse<PbMotorPwmUpperLimitSet.MotorPwmUpperLimitSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbMotorPwmUpperLimitSet.MotorPwmUpperLimitSetResponse data;
        try {
            PbMotorPwmUpperLimitSet.MotorPwmUpperLimitSetResponse response = PbMotorPwmUpperLimitSet.MotorPwmUpperLimitSetResponse.parseFrom(bizData);
            // TODO check the response
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbMotorPwmUpperLimitSet.MotorPwmUpperLimitSetRequest.Builder builder = PbMotorPwmUpperLimitSet.MotorPwmUpperLimitSetRequest.newBuilder();
        /* TODO set request params to pb
        builder.setPath(request.getParameter("path", ""));
        */
        return builder.build().toByteArray();
    }

}
