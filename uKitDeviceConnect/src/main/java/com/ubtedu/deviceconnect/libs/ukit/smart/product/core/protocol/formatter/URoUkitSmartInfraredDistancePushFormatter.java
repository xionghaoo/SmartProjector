package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbInfraredDistancePush;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/10/24
 * @CmdId 1003
 * @FunctionName infrared_distance_push
 * @Description 红外距离值上报
 * @FileName URoUkitSmartInfraredDistancePushFormatter.java
 * 
 **/
 public class URoUkitSmartInfraredDistancePushFormatter extends URoUkitSmartCommandFormatter<PbInfraredDistancePush.InfraredDistancePushResponse> {

    private URoUkitSmartInfraredDistancePushFormatter(){}

    public static final URoUkitSmartInfraredDistancePushFormatter INSTANCE = new URoUkitSmartInfraredDistancePushFormatter();

    @Override
    public URoResponse<PbInfraredDistancePush.InfraredDistancePushResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbInfraredDistancePush.InfraredDistancePushResponse data;
        try {
            PbInfraredDistancePush.InfraredDistancePushResponse response = PbInfraredDistancePush.InfraredDistancePushResponse.parseFrom(bizData);
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
