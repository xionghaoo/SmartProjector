package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbMotorStop;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1001
 * @FunctionName motor_stop
 * @Description 停止转动
 * @FileName URoUkitSmartMotorStopFormatter.java
 * 
 **/
public class URoUkitSmartMotorStopFormatter extends URoUkitSmartCommandFormatter<PbMotorStop.MotorStopResponse> {

    private URoUkitSmartMotorStopFormatter(){}

    public static final URoUkitSmartMotorStopFormatter INSTANCE = new URoUkitSmartMotorStopFormatter();

    @Override
    public URoResponse<PbMotorStop.MotorStopResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbMotorStop.MotorStopResponse data;
        try {
            PbMotorStop.MotorStopResponse response = PbMotorStop.MotorStopResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        return EMPTY_REQUEST_DATA;
    }

}
