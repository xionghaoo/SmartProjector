package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLcdIcon;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/06/22
 * @CmdId 1007
 * @FunctionName lcd_icon
 * @Description 显示图标
 * @FileName URoUkitSmartLcdIconFormatter.java
 * 
 **/
 public class URoUkitSmartLcdIconFormatter extends URoUkitSmartCommandFormatter<PbLcdIcon.LcdIconResponse> {

    private URoUkitSmartLcdIconFormatter(){}

    public static final URoUkitSmartLcdIconFormatter INSTANCE = new URoUkitSmartLcdIconFormatter();

    @Override
    public URoResponse<PbLcdIcon.LcdIconResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbLcdIcon.LcdIconResponse data;
        try {
            PbLcdIcon.LcdIconResponse response = PbLcdIcon.LcdIconResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbLcdIcon.LcdIconRequest.Builder builder = PbLcdIcon.LcdIconRequest.newBuilder();
        builder.setType(request.getParameter("type", 0));
        builder.setIndex(request.getParameter("index", 0));
        builder.setPostX(request.getParameter("post_x", 0));
        builder.setPostY(request.getParameter("post_y", 0));
        return builder.build().toByteArray();
    }

}
