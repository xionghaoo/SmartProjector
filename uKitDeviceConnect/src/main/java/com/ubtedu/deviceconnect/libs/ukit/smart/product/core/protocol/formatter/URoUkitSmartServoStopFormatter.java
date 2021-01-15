package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbServoStop;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1004
 * @FunctionName servo_stop
 * @Description 停止运动
 * @FileName URoUkitSmartServoStopFormatter.java
 * 
 **/
public class URoUkitSmartServoStopFormatter extends URoUkitSmartCommandFormatter<PbServoStop.ServoStopResponse> {

    private URoUkitSmartServoStopFormatter(){}

    public static final URoUkitSmartServoStopFormatter INSTANCE = new URoUkitSmartServoStopFormatter();

    @Override
    public URoResponse<PbServoStop.ServoStopResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbServoStop.ServoStopResponse data;
        try {
            PbServoStop.ServoStopResponse response = PbServoStop.ServoStopResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbServoStop.ServoStopRequest.Builder builder = PbServoStop.ServoStopRequest.newBuilder();
        builder.setMode(request.getParameter("mode", 0));
        return builder.build().toByteArray();
    }

}
