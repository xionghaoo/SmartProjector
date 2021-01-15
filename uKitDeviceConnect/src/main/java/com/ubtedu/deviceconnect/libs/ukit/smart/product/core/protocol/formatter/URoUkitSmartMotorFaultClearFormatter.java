package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbMotorFaultClear;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1009
 * @FunctionName motor_fault_clear
 * @Description 清除电机异常
 * @FileName URoUkitSmartMotorFaultClearFormatter.java
 * 
 **/
public class URoUkitSmartMotorFaultClearFormatter extends URoUkitSmartCommandFormatter<PbMotorFaultClear.MotorFaultClearResponse> {

    private URoUkitSmartMotorFaultClearFormatter(){}

    public static final URoUkitSmartMotorFaultClearFormatter INSTANCE = new URoUkitSmartMotorFaultClearFormatter();

    @Override
    public URoResponse<PbMotorFaultClear.MotorFaultClearResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbMotorFaultClear.MotorFaultClearResponse data;
        try {
            PbMotorFaultClear.MotorFaultClearResponse response = PbMotorFaultClear.MotorFaultClearResponse.parseFrom(bizData);
            // TODO check the response
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbMotorFaultClear.MotorFaultClearRequest.Builder builder = PbMotorFaultClear.MotorFaultClearRequest.newBuilder();
        /* TODO set request params to pb
        builder.setPath(request.getParameter("path", ""));
        */
        return builder.build().toByteArray();
    }

}
