package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbTouchHysteresisTimeSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1002
 * @FunctionName touch_hysteresis_time_set
 * @Description 设置触碰按下类型的迟滞时间
 * @FileName URoUkitSmartTouchHysteresisTimeSetFormatter.java
 * 
 **/
public class URoUkitSmartTouchHysteresisTimeSetFormatter extends URoUkitSmartCommandFormatter<PbTouchHysteresisTimeSet.TouchHysteresisTimeSetResponse> {

    private URoUkitSmartTouchHysteresisTimeSetFormatter(){}

    public static final URoUkitSmartTouchHysteresisTimeSetFormatter INSTANCE = new URoUkitSmartTouchHysteresisTimeSetFormatter();

    @Override
    public URoResponse<PbTouchHysteresisTimeSet.TouchHysteresisTimeSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbTouchHysteresisTimeSet.TouchHysteresisTimeSetResponse data;
        try {
            PbTouchHysteresisTimeSet.TouchHysteresisTimeSetResponse response = PbTouchHysteresisTimeSet.TouchHysteresisTimeSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbTouchHysteresisTimeSet.TouchHysteresisTimeSetRequest.Builder builder = PbTouchHysteresisTimeSet.TouchHysteresisTimeSetRequest.newBuilder();
        builder.setHysteresisTime(request.getParameter("hysteresis_time", 0));
        return builder.build().toByteArray();
    }

}
