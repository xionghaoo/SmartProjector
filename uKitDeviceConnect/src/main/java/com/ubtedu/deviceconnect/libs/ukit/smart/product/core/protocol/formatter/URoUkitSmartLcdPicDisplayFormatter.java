package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLcdPicDisplay;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/06/22
 * @CmdId 1009
 * @FunctionName lcd_pic_display
 * @Description 显示自定义图片
 * @FileName URoUkitSmartLcdPicDisplayFormatter.java
 * 
 **/
 public class URoUkitSmartLcdPicDisplayFormatter extends URoUkitSmartCommandFormatter<PbLcdPicDisplay.LcdPicDisplayResponse> {

    private URoUkitSmartLcdPicDisplayFormatter(){}

    public static final URoUkitSmartLcdPicDisplayFormatter INSTANCE = new URoUkitSmartLcdPicDisplayFormatter();

    @Override
    public URoResponse<PbLcdPicDisplay.LcdPicDisplayResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbLcdPicDisplay.LcdPicDisplayResponse data;
        try {
            PbLcdPicDisplay.LcdPicDisplayResponse response = PbLcdPicDisplay.LcdPicDisplayResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbLcdPicDisplay.LcdPicDisplayRequest.Builder builder = PbLcdPicDisplay.LcdPicDisplayRequest.newBuilder();
        builder.setType(request.getParameter("type", 0));
        builder.setName(request.getParameter("name", ""));
        return builder.build().toByteArray();
    }

}
