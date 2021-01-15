package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbServoInfoGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1011
 * @FunctionName servo_info_get
 * @Description 获取舵机信息
 * @FileName URoUkitSmartServoInfoGetFormatter.java
 * 
 **/
public class URoUkitSmartServoInfoGetFormatter extends URoUkitSmartCommandFormatter<PbServoInfoGet.ServoInfoGetResponse> {

    private URoUkitSmartServoInfoGetFormatter(){}

    public static final URoUkitSmartServoInfoGetFormatter INSTANCE = new URoUkitSmartServoInfoGetFormatter();

    @Override
    public URoResponse<PbServoInfoGet.ServoInfoGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbServoInfoGet.ServoInfoGetResponse data;
        try {
            PbServoInfoGet.ServoInfoGetResponse response = PbServoInfoGet.ServoInfoGetResponse.parseFrom(bizData);
            // TODO check the response
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbServoInfoGet.ServoInfoGetRequest.Builder builder = PbServoInfoGet.ServoInfoGetRequest.newBuilder();
        /* TODO set request params to pb
        builder.setPath(request.getParameter("path", ""));
        */
        return builder.build().toByteArray();
    }

}
