package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbMotorCntReset;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1007
 * @FunctionName motor_cnt_reset
 * @Description 复位马达码盘计数值
 * @FileName URoUkitSmartMotorCntResetFormatter.java
 * 
 **/
public class URoUkitSmartMotorCntResetFormatter extends URoUkitSmartCommandFormatter<PbMotorCntReset.MotorCntResetResponse> {

    private URoUkitSmartMotorCntResetFormatter(){}

    public static final URoUkitSmartMotorCntResetFormatter INSTANCE = new URoUkitSmartMotorCntResetFormatter();

    @Override
    public URoResponse<PbMotorCntReset.MotorCntResetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbMotorCntReset.MotorCntResetResponse data;
        try {
            PbMotorCntReset.MotorCntResetResponse response = PbMotorCntReset.MotorCntResetResponse.parseFrom(bizData);
            // TODO check the response
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbMotorCntReset.MotorCntResetRequest.Builder builder = PbMotorCntReset.MotorCntResetRequest.newBuilder();
        /* TODO set request params to pb
        builder.setPath(request.getParameter("path", ""));
        */
        return builder.build().toByteArray();
    }

}
