package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbMotorCntGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1006
 * @FunctionName motor_cnt_get
 * @Description 获取马达码盘计数值
 * @FileName URoUkitSmartMotorCntGetFormatter.java
 * 
 **/
public class URoUkitSmartMotorCntGetFormatter extends URoUkitSmartCommandFormatter<PbMotorCntGet.MotorCntGetResponse> {

    private URoUkitSmartMotorCntGetFormatter(){}

    public static final URoUkitSmartMotorCntGetFormatter INSTANCE = new URoUkitSmartMotorCntGetFormatter();

    @Override
    public URoResponse<PbMotorCntGet.MotorCntGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbMotorCntGet.MotorCntGetResponse data;
        try {
            PbMotorCntGet.MotorCntGetResponse response = PbMotorCntGet.MotorCntGetResponse.parseFrom(bizData);
            // TODO check the response
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbMotorCntGet.MotorCntGetRequest.Builder builder = PbMotorCntGet.MotorCntGetRequest.newBuilder();
        /* TODO set request params to pb
        builder.setPath(request.getParameter("path", ""));
        */
        return builder.build().toByteArray();
    }

}
