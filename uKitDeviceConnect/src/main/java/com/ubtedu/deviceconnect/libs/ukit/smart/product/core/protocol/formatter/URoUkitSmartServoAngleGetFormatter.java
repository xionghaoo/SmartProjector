package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbServoAngleGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1001
 * @FunctionName servo_angle_get
 * @Description 角度回读
 * @FileName URoUkitSmartServoAngleGetFormatter.java
 * 
 **/
public class URoUkitSmartServoAngleGetFormatter extends URoUkitSmartCommandFormatter<PbServoAngleGet.ServoAngleGetResponse> {

    private URoUkitSmartServoAngleGetFormatter(){}

    public static final URoUkitSmartServoAngleGetFormatter INSTANCE = new URoUkitSmartServoAngleGetFormatter();

    @Override
    public URoResponse<PbServoAngleGet.ServoAngleGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbServoAngleGet.ServoAngleGetResponse data;
        try {
            PbServoAngleGet.ServoAngleGetResponse response = PbServoAngleGet.ServoAngleGetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbServoAngleGet.ServoAngleGetRequest.Builder builder = PbServoAngleGet.ServoAngleGetRequest.newBuilder();
        builder.setPwr(request.getParameter("pwr", true));
        return builder.build().toByteArray();
    }

}
