package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbTouchTypePush;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/10/24
 * @CmdId 1006
 * @FunctionName touch_type_push
 * @Description 读取触碰按下类型
 * @FileName URoUkitSmartTouchTypePushFormatter.java
 * 
 **/
 public class URoUkitSmartTouchTypePushFormatter extends URoUkitSmartCommandFormatter<PbTouchTypePush.TouchTypePushResponse> {

    private URoUkitSmartTouchTypePushFormatter(){}

    public static final URoUkitSmartTouchTypePushFormatter INSTANCE = new URoUkitSmartTouchTypePushFormatter();

    @Override
    public URoResponse<PbTouchTypePush.TouchTypePushResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbTouchTypePush.TouchTypePushResponse data;
        try {
            PbTouchTypePush.TouchTypePushResponse response = PbTouchTypePush.TouchTypePushResponse.parseFrom(bizData);
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
