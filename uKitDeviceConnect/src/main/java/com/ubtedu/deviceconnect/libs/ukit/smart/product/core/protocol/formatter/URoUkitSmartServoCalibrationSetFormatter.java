package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbServoCalibrationSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1005
 * @FunctionName servo_calibration_set
 * @Description 设置舵机角度校正值
 * @FileName URoUkitSmartServoCalibrationSetFormatter.java
 * 
 **/
public class URoUkitSmartServoCalibrationSetFormatter extends URoUkitSmartCommandFormatter<PbServoCalibrationSet.ServoCalibrationSetResponse> {

    private URoUkitSmartServoCalibrationSetFormatter(){}

    public static final URoUkitSmartServoCalibrationSetFormatter INSTANCE = new URoUkitSmartServoCalibrationSetFormatter();

    @Override
    public URoResponse<PbServoCalibrationSet.ServoCalibrationSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbServoCalibrationSet.ServoCalibrationSetResponse data;
        try {
            PbServoCalibrationSet.ServoCalibrationSetResponse response = PbServoCalibrationSet.ServoCalibrationSetResponse.parseFrom(bizData);
            // TODO check the response
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbServoCalibrationSet.ServoCalibrationSetRequest.Builder builder = PbServoCalibrationSet.ServoCalibrationSetRequest.newBuilder();
        /* TODO set request params to pb
        builder.setPath(request.getParameter("path", ""));
        */
        return builder.build().toByteArray();
    }

}
