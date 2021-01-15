package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLcdTestPic;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/06/30
 * @CmdId 1013
 * @FunctionName lcd_test_pic
 * @Description 显示测试图片
 * @FileName URoUkitSmartLcdTestPicFormatter.java
 * 
 **/
 public class URoUkitSmartLcdTestPicFormatter extends URoUkitSmartCommandFormatter<PbLcdTestPic.LcdTestPicResponse> {

    private URoUkitSmartLcdTestPicFormatter(){}

    public static final URoUkitSmartLcdTestPicFormatter INSTANCE = new URoUkitSmartLcdTestPicFormatter();

    @Override
    public URoResponse<PbLcdTestPic.LcdTestPicResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbLcdTestPic.LcdTestPicResponse data;
        try {
            PbLcdTestPic.LcdTestPicResponse response = PbLcdTestPic.LcdTestPicResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbLcdTestPic.LcdTestPicRequest.Builder builder = PbLcdTestPic.LcdTestPicRequest.newBuilder();
        builder.setIndex(request.getParameter("index", 0));
        return builder.build().toByteArray();
    }

}
