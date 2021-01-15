package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLcdInnerPic;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/06/22
 * @CmdId 1008
 * @FunctionName lcd_inner_pic
 * @Description 显示内置图片
 * @FileName URoUkitSmartLcdInnerPicFormatter.java
 * 
 **/
 public class URoUkitSmartLcdInnerPicFormatter extends URoUkitSmartCommandFormatter<PbLcdInnerPic.LcdInnerPicResponse> {

    private URoUkitSmartLcdInnerPicFormatter(){}

    public static final URoUkitSmartLcdInnerPicFormatter INSTANCE = new URoUkitSmartLcdInnerPicFormatter();

    @Override
    public URoResponse<PbLcdInnerPic.LcdInnerPicResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbLcdInnerPic.LcdInnerPicResponse data;
        try {
            PbLcdInnerPic.LcdInnerPicResponse response = PbLcdInnerPic.LcdInnerPicResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbLcdInnerPic.LcdInnerPicRequest.Builder builder = PbLcdInnerPic.LcdInnerPicRequest.newBuilder();
        builder.setIndex(request.getParameter("index", 0));
        builder.setPostX(request.getParameter("post_x", 0));
        builder.setPostY(request.getParameter("post_y", 0));
        return builder.build().toByteArray();
    }

}
