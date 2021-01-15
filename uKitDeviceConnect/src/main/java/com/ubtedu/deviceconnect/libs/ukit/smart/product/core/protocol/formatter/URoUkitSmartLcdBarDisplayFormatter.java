package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLcdBarDisplay;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/06/22
 * @CmdId 1004
 * @FunctionName lcd_bar_display
 * @Description 显示状态栏
 * @FileName URoUkitSmartLcdBarDisplayFormatter.java
 * 
 **/
 public class URoUkitSmartLcdBarDisplayFormatter extends URoUkitSmartCommandFormatter<PbLcdBarDisplay.LcdBarDisplayResponse> {

    private URoUkitSmartLcdBarDisplayFormatter(){}

    public static final URoUkitSmartLcdBarDisplayFormatter INSTANCE = new URoUkitSmartLcdBarDisplayFormatter();

    @Override
    public URoResponse<PbLcdBarDisplay.LcdBarDisplayResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbLcdBarDisplay.LcdBarDisplayResponse data;
        try {
            PbLcdBarDisplay.LcdBarDisplayResponse response = PbLcdBarDisplay.LcdBarDisplayResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbLcdBarDisplay.LcdBarDisplayRequest.Builder builder = PbLcdBarDisplay.LcdBarDisplayRequest.newBuilder();
        builder.setStatus(request.getParameter("status", false) ? 1 : 0);
        String text = request.getParameter("text", "");
        builder.setText(ByteString.copyFrom(text.getBytes("GBK")));
        return builder.build().toByteArray();
    }

}
