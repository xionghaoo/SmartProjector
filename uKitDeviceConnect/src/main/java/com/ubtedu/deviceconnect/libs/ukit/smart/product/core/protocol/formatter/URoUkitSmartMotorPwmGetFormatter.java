package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbMotorPwmGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1005
 * @FunctionName motor_pwm_get
 * @Description 获取马达pwm值
 * @FileName URoUkitSmartMotorPwmGetFormatter.java
 * 
 **/
public class URoUkitSmartMotorPwmGetFormatter extends URoUkitSmartCommandFormatter<PbMotorPwmGet.MotorPwmGetResponse> {

    private URoUkitSmartMotorPwmGetFormatter(){}

    public static final URoUkitSmartMotorPwmGetFormatter INSTANCE = new URoUkitSmartMotorPwmGetFormatter();

    @Override
    public URoResponse<PbMotorPwmGet.MotorPwmGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbMotorPwmGet.MotorPwmGetResponse data;
        try {
            PbMotorPwmGet.MotorPwmGetResponse response = PbMotorPwmGet.MotorPwmGetResponse.parseFrom(bizData);
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
