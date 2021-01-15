package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLcdRollText;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/06/22
 * @CmdId 1006
 * @FunctionName lcd_roll_text
 * @Description 动态文字
 * @FileName URoUkitSmartLcdRollTextFormatter.java
 * 
 **/
 public class URoUkitSmartLcdRollTextFormatter extends URoUkitSmartCommandFormatter<PbLcdRollText.LcdRollTextResponse> {

    private URoUkitSmartLcdRollTextFormatter(){}

    public static final URoUkitSmartLcdRollTextFormatter INSTANCE = new URoUkitSmartLcdRollTextFormatter();

    @Override
    public URoResponse<PbLcdRollText.LcdRollTextResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbLcdRollText.LcdRollTextResponse data;
        try {
            PbLcdRollText.LcdRollTextResponse response = PbLcdRollText.LcdRollTextResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbLcdRollText.LcdRollTextRequest.Builder builder = PbLcdRollText.LcdRollTextRequest.newBuilder();
        String text = request.getParameter("text", "");
        builder.setText(ByteString.copyFrom(text.getBytes("GBK")));
        builder.setColor(request.getParameter("color", 0));
        builder.setPostX(request.getParameter("post_x", 0));
        builder.setPostY(request.getParameter("post_y", 0));
        return builder.build().toByteArray();
    }

}
