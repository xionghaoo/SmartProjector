package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbServoCalibrationGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/20
 * @CmdId 1006
 * @FunctionName servo_calibration_get
 * @Description 读取舵机角度校正值
 * @FileName URoUkitSmartServoCalibrationGetFormatter.java
 * 
 **/
public class URoUkitSmartServoCalibrationGetFormatter extends URoUkitSmartCommandFormatter<PbServoCalibrationGet.ServoCalibrationGetResponse> {

    private URoUkitSmartServoCalibrationGetFormatter(){}

    public static final URoUkitSmartServoCalibrationGetFormatter INSTANCE = new URoUkitSmartServoCalibrationGetFormatter();

    @Override
    public URoResponse<PbServoCalibrationGet.ServoCalibrationGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbServoCalibrationGet.ServoCalibrationGetResponse data;
        try {
            PbServoCalibrationGet.ServoCalibrationGetResponse response = PbServoCalibrationGet.ServoCalibrationGetResponse.parseFrom(bizData);
            // TODO check the response
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbServoCalibrationGet.ServoCalibrationGetRequest.Builder builder = PbServoCalibrationGet.ServoCalibrationGetRequest.newBuilder();
        /* TODO set request params to pb
        builder.setPath(request.getParameter("path", ""));
        */
        return builder.build().toByteArray();
    }

}
