package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbServoPwmSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1002
 * @FunctionName servo_pwm_set
 * @Description 恒pwm模式
 * @FileName URoUkitSmartServoPwmSetFormatter.java
 * 
 **/
public class URoUkitSmartServoPwmSetFormatter extends URoUkitSmartCommandFormatter<PbServoPwmSet.ServoPwmSetResponse> {

    private URoUkitSmartServoPwmSetFormatter(){}

    public static final URoUkitSmartServoPwmSetFormatter INSTANCE = new URoUkitSmartServoPwmSetFormatter();

    @Override
    public URoResponse<PbServoPwmSet.ServoPwmSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbServoPwmSet.ServoPwmSetResponse data;
        try {
            PbServoPwmSet.ServoPwmSetResponse response = PbServoPwmSet.ServoPwmSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbServoPwmSet.ServoPwmSetRequest.Builder builder = PbServoPwmSet.ServoPwmSetRequest.newBuilder();
        builder.setPwm(request.getParameter("pwm", 0));
        return builder.build().toByteArray();
    }

}
