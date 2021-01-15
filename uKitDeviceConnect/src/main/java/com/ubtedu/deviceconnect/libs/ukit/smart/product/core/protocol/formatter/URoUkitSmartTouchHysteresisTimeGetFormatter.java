package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbTouchHysteresisTimeGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1003
 * @FunctionName touch_hysteresis_time_get
 * @Description 回读触碰按下类型的迟滞时间
 * @FileName URoUkitSmartTouchHysteresisTimeGetFormatter.java
 * 
 **/
public class URoUkitSmartTouchHysteresisTimeGetFormatter extends URoUkitSmartCommandFormatter<PbTouchHysteresisTimeGet.TouchHysteresisTimeGetResponse> {

    private URoUkitSmartTouchHysteresisTimeGetFormatter(){}

    public static final URoUkitSmartTouchHysteresisTimeGetFormatter INSTANCE = new URoUkitSmartTouchHysteresisTimeGetFormatter();

    @Override
    public URoResponse<PbTouchHysteresisTimeGet.TouchHysteresisTimeGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbTouchHysteresisTimeGet.TouchHysteresisTimeGetResponse data;
        try {
            PbTouchHysteresisTimeGet.TouchHysteresisTimeGetResponse response = PbTouchHysteresisTimeGet.TouchHysteresisTimeGetResponse.parseFrom(bizData);
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
