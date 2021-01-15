package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbColorRgbGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1000
 * @FunctionName color_rgb_get
 * @Description 读取颜色值
 * @FileName URoUkitSmartColorRgbGetFormatter.java
 * 
 **/
public class URoUkitSmartColorRgbGetFormatter extends URoUkitSmartCommandFormatter<PbColorRgbGet.ColorRgbGetResponse> {

    private URoUkitSmartColorRgbGetFormatter(){}

    public static final URoUkitSmartColorRgbGetFormatter INSTANCE = new URoUkitSmartColorRgbGetFormatter();

    @Override
    public URoResponse<PbColorRgbGet.ColorRgbGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbColorRgbGet.ColorRgbGetResponse data;
        try {
            PbColorRgbGet.ColorRgbGetResponse response = PbColorRgbGet.ColorRgbGetResponse.parseFrom(bizData);
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
