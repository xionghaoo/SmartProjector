package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbServoFaultClear;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1008
 * @FunctionName servo_fault_clear
 * @Description 清除舵机异常
 * @FileName URoUkitSmartServoFaultClearFormatter.java
 * 
 **/
public class URoUkitSmartServoFaultClearFormatter extends URoUkitSmartCommandFormatter<PbServoFaultClear.ServoFaultClearResponse> {

    private URoUkitSmartServoFaultClearFormatter(){}

    public static final URoUkitSmartServoFaultClearFormatter INSTANCE = new URoUkitSmartServoFaultClearFormatter();

    @Override
    public URoResponse<PbServoFaultClear.ServoFaultClearResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbServoFaultClear.ServoFaultClearResponse data;
        try {
            PbServoFaultClear.ServoFaultClearResponse response = PbServoFaultClear.ServoFaultClearResponse.parseFrom(bizData);
            // TODO check the response
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbServoFaultClear.ServoFaultClearRequest.Builder builder = PbServoFaultClear.ServoFaultClearRequest.newBuilder();
        /* TODO set request params to pb
        builder.setPath(request.getParameter("path", ""));
        */
        return builder.build().toByteArray();
    }

}
