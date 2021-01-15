package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbServoAngleSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1000
 * @FunctionName servo_angle_set
 * @Description 转动到指定位置
 * @FileName URoUkitSmartServoAngleSetFormatter.java
 * 
 **/
public class URoUkitSmartServoAngleSetFormatter extends URoUkitSmartCommandFormatter<PbServoAngleSet.ServoAngleSetResponse> {

    private URoUkitSmartServoAngleSetFormatter(){}

    public static final URoUkitSmartServoAngleSetFormatter INSTANCE = new URoUkitSmartServoAngleSetFormatter();

    @Override
    public URoResponse<PbServoAngleSet.ServoAngleSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbServoAngleSet.ServoAngleSetResponse data;
        try {
            PbServoAngleSet.ServoAngleSetResponse response = PbServoAngleSet.ServoAngleSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbServoAngleSet.ServoAngleSetRequest.Builder builder = PbServoAngleSet.ServoAngleSetRequest.newBuilder();
        builder.setTarAngle(request.getParameter("tar_angle", 0));
        builder.setRotationTime(request.getParameter("rotation_time", 0));
        builder.setShieldingTime(request.getParameter("shielding_time", 0));
        builder.setMode(request.getParameter("mode", 0));
        return builder.build().toByteArray();
    }

}
