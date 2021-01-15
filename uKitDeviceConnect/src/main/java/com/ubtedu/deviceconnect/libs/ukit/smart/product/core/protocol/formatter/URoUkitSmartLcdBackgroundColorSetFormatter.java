package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLcdBackgroundColorSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/06/22
 * @CmdId 1003
 * @FunctionName lcd_background_color_set
 * @Description 设置背景色
 * @FileName URoUkitSmartLcdBackgroundColorSetFormatter.java
 * 
 **/
 public class URoUkitSmartLcdBackgroundColorSetFormatter extends URoUkitSmartCommandFormatter<PbLcdBackgroundColorSet.LcdBackgroundColorSetResponse> {

    private URoUkitSmartLcdBackgroundColorSetFormatter(){}

    public static final URoUkitSmartLcdBackgroundColorSetFormatter INSTANCE = new URoUkitSmartLcdBackgroundColorSetFormatter();

    @Override
    public URoResponse<PbLcdBackgroundColorSet.LcdBackgroundColorSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbLcdBackgroundColorSet.LcdBackgroundColorSetResponse data;
        try {
            PbLcdBackgroundColorSet.LcdBackgroundColorSetResponse response = PbLcdBackgroundColorSet.LcdBackgroundColorSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbLcdBackgroundColorSet.LcdBackgroundColorSetRequest.Builder builder = PbLcdBackgroundColorSet.LcdBackgroundColorSetRequest.newBuilder();
        builder.setColor(request.getParameter("color", 0));
        return builder.build().toByteArray();
    }

}
