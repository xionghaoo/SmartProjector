package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbServoStatusGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1010
 * @FunctionName servo_status_get
 * @Description 获取舵机状态
 * @FileName URoUkitSmartServoStatusGetFormatter.java
 * 
 **/
public class URoUkitSmartServoStatusGetFormatter extends URoUkitSmartCommandFormatter<PbServoStatusGet.ServoStatusGetResponse> {

    private URoUkitSmartServoStatusGetFormatter(){}

    public static final URoUkitSmartServoStatusGetFormatter INSTANCE = new URoUkitSmartServoStatusGetFormatter();

    @Override
    public URoResponse<PbServoStatusGet.ServoStatusGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbServoStatusGet.ServoStatusGetResponse data;
        try {
            PbServoStatusGet.ServoStatusGetResponse response = PbServoStatusGet.ServoStatusGetResponse.parseFrom(bizData);
            // TODO check the response
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbServoStatusGet.ServoStatusGetRequest.Builder builder = PbServoStatusGet.ServoStatusGetRequest.newBuilder();
        /* TODO set request params to pb
        builder.setPath(request.getParameter("path", ""));
        */
        return builder.build().toByteArray();
    }

}
