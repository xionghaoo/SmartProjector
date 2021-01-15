package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbUltrasonicLightSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/22
 * @CmdId 1001
 * @FunctionName ultrasonic_light_set
 * @Description 设置灯光
 * @FileName URoUkitSmartUltrasonicLightSetFormatter.java
 * 
 **/
public class URoUkitSmartUltrasonicLightSetFormatter extends URoUkitSmartCommandFormatter<PbUltrasonicLightSet.UltrasonicLightSetResponse> {

    private URoUkitSmartUltrasonicLightSetFormatter(){}

    public static final URoUkitSmartUltrasonicLightSetFormatter INSTANCE = new URoUkitSmartUltrasonicLightSetFormatter();

    @Override
    public URoResponse<PbUltrasonicLightSet.UltrasonicLightSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbUltrasonicLightSet.UltrasonicLightSetResponse data;
        try {
            PbUltrasonicLightSet.UltrasonicLightSetResponse response = PbUltrasonicLightSet.UltrasonicLightSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbUltrasonicLightSet.UltrasonicLightSetRequest.Builder builder = PbUltrasonicLightSet.UltrasonicLightSetRequest.newBuilder();
        builder.setR(request.getParameter("r", 0));
        builder.setG(request.getParameter("g", 0));
        builder.setB(request.getParameter("b", 0));
        builder.setMode(request.getParameter("mode", 0));
        builder.setSpeed(request.getParameter("speed", 0));
        builder.setTime(request.getParameter("time", 0));
        return builder.build().toByteArray();
    }

}
