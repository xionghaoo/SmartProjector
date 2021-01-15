package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbServoFaultGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1007
 * @FunctionName servo_fault_get
 * @Description 获取舵机异常
 * @FileName URoUkitSmartServoFaultGetFormatter.java
 * 
 **/
public class URoUkitSmartServoFaultGetFormatter extends URoUkitSmartCommandFormatter<PbServoFaultGet.ServoFaultGetResponse> {

    private URoUkitSmartServoFaultGetFormatter(){}

    public static final URoUkitSmartServoFaultGetFormatter INSTANCE = new URoUkitSmartServoFaultGetFormatter();

    @Override
    public URoResponse<PbServoFaultGet.ServoFaultGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbServoFaultGet.ServoFaultGetResponse data;
        try {
            PbServoFaultGet.ServoFaultGetResponse response = PbServoFaultGet.ServoFaultGetResponse.parseFrom(bizData);
            // TODO check the response
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbServoFaultGet.ServoFaultGetRequest.Builder builder = PbServoFaultGet.ServoFaultGetRequest.newBuilder();
        /* TODO set request params to pb
        builder.setPath(request.getParameter("path", ""));
        */
        return builder.build().toByteArray();
    }

}
