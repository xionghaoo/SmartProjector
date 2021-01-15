package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbColorRgbPush;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/10/24
 * @CmdId 1003
 * @FunctionName color_rgb_push
 * @Description 读取颜色值
 * @FileName URoUkitSmartColorRgbPushFormatter.java
 * 
 **/
 public class URoUkitSmartColorRgbPushFormatter extends URoUkitSmartCommandFormatter<PbColorRgbPush.ColorRgbPushResponse> {

    private URoUkitSmartColorRgbPushFormatter(){}

    public static final URoUkitSmartColorRgbPushFormatter INSTANCE = new URoUkitSmartColorRgbPushFormatter();

    @Override
    public URoResponse<PbColorRgbPush.ColorRgbPushResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbColorRgbPush.ColorRgbPushResponse data;
        try {
            PbColorRgbPush.ColorRgbPushResponse response = PbColorRgbPush.ColorRgbPushResponse.parseFrom(bizData);
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
