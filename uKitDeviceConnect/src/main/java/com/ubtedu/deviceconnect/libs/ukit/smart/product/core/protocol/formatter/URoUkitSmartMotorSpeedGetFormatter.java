package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbMotorSpeedGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1002
 * @FunctionName motor_speed_get
 * @Description 获取转动速度
 * @FileName URoUkitSmartMotorSpeedGetFormatter.java
 * 
 **/
public class URoUkitSmartMotorSpeedGetFormatter extends URoUkitSmartCommandFormatter<PbMotorSpeedGet.MotorSpeedGetResponse> {

    private URoUkitSmartMotorSpeedGetFormatter(){}

    public static final URoUkitSmartMotorSpeedGetFormatter INSTANCE = new URoUkitSmartMotorSpeedGetFormatter();

    @Override
    public URoResponse<PbMotorSpeedGet.MotorSpeedGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbMotorSpeedGet.MotorSpeedGetResponse data;
        try {
            PbMotorSpeedGet.MotorSpeedGetResponse response = PbMotorSpeedGet.MotorSpeedGetResponse.parseFrom(bizData);
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
