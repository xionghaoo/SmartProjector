package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbMotorFaultGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1008
 * @FunctionName motor_fault_get
 * @Description 获取电机异常
 * @FileName URoUkitSmartMotorFaultGetFormatter.java
 * 
 **/
public class URoUkitSmartMotorFaultGetFormatter extends URoUkitSmartCommandFormatter<PbMotorFaultGet.MotorFaultGetResponse> {

    private URoUkitSmartMotorFaultGetFormatter(){}

    public static final URoUkitSmartMotorFaultGetFormatter INSTANCE = new URoUkitSmartMotorFaultGetFormatter();

    @Override
    public URoResponse<PbMotorFaultGet.MotorFaultGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbMotorFaultGet.MotorFaultGetResponse data;
        try {
            PbMotorFaultGet.MotorFaultGetResponse response = PbMotorFaultGet.MotorFaultGetResponse.parseFrom(bizData);
            // TODO check the response
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbMotorFaultGet.MotorFaultGetRequest.Builder builder = PbMotorFaultGet.MotorFaultGetRequest.newBuilder();
        /* TODO set request params to pb
        builder.setPath(request.getParameter("path", ""));
        */
        return builder.build().toByteArray();
    }

}
