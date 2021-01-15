package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbTouchTimesGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId: 1000
 * @FunctionName：touch_times_get
 * @Description：读取触碰按下次数
 * @FileName: URoUkitSmartTouchTimesGetFormatter.java
 * 
 **/
public class URoUkitSmartTouchTimesGetFormatter extends URoUkitSmartCommandFormatter<PbTouchTimesGet.TouchTimesGetResponse> {

    private URoUkitSmartTouchTimesGetFormatter(){}

    public static final URoUkitSmartTouchTimesGetFormatter INSTANCE = new URoUkitSmartTouchTimesGetFormatter();

    @Override
    public URoResponse<PbTouchTimesGet.TouchTimesGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbTouchTimesGet.TouchTimesGetResponse data;
        try {
            PbTouchTimesGet.TouchTimesGetResponse response = PbTouchTimesGet.TouchTimesGetResponse.parseFrom(bizData);
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
