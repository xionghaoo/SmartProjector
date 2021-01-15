package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLcdStaticText;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/06/22
 * @CmdId 1005
 * @FunctionName lcd_static_text
 * @Description 静态文字
 * @FileName URoUkitSmartLcdStaticTextFormatter.java
 * 
 **/
 public class URoUkitSmartLcdStaticTextFormatter extends URoUkitSmartCommandFormatter<PbLcdStaticText.LcdStaticTextResponse> {

    private URoUkitSmartLcdStaticTextFormatter(){}

    public static final URoUkitSmartLcdStaticTextFormatter INSTANCE = new URoUkitSmartLcdStaticTextFormatter();

    @Override
    public URoResponse<PbLcdStaticText.LcdStaticTextResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbLcdStaticText.LcdStaticTextResponse data;
        try {
            PbLcdStaticText.LcdStaticTextResponse response = PbLcdStaticText.LcdStaticTextResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbLcdStaticText.LcdStaticTextRequest.Builder builder = PbLcdStaticText.LcdStaticTextRequest.newBuilder();
        String text = request.getParameter("text", "");
        builder.setText(ByteString.copyFrom(text.getBytes("GBK")));
        builder.setColor(request.getParameter("color", 0));
        builder.setPostX(request.getParameter("post_x", 0));
        builder.setPostY(request.getParameter("post_y", 0));
        return builder.build().toByteArray();
    }

}
