package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbServoLimitSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1009
 * @FunctionName servo_limit_set
 * @Description 舵机限位设置
 * @FileName URoUkitSmartServoLimitSetFormatter.java
 * 
 **/
public class URoUkitSmartServoLimitSetFormatter extends URoUkitSmartCommandFormatter<PbServoLimitSet.ServoLimitSetResponse> {

    private URoUkitSmartServoLimitSetFormatter(){}

    public static final URoUkitSmartServoLimitSetFormatter INSTANCE = new URoUkitSmartServoLimitSetFormatter();

    @Override
    public URoResponse<PbServoLimitSet.ServoLimitSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbServoLimitSet.ServoLimitSetResponse data;
        try {
            PbServoLimitSet.ServoLimitSetResponse response = PbServoLimitSet.ServoLimitSetResponse.parseFrom(bizData);
            // TODO check the response
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbServoLimitSet.ServoLimitSetRequest.Builder builder = PbServoLimitSet.ServoLimitSetRequest.newBuilder();
        /* TODO set request params to pb
        builder.setPath(request.getParameter("path", ""));
        */
        return builder.build().toByteArray();
    }

}
