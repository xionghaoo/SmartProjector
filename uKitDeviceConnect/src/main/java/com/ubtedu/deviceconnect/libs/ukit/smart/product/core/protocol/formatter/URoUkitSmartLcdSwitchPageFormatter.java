package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLcdSwitchPage;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/06/30
 * @CmdId 1011
 * @FunctionName lcd_switch_page
 * @Description 切换页面
 * @FileName URoUkitSmartLcdSwitchPageFormatter.java
 * 
 **/
 public class URoUkitSmartLcdSwitchPageFormatter extends URoUkitSmartCommandFormatter<PbLcdSwitchPage.LcdSwitchPageResponse> {

    private URoUkitSmartLcdSwitchPageFormatter(){}

    public static final URoUkitSmartLcdSwitchPageFormatter INSTANCE = new URoUkitSmartLcdSwitchPageFormatter();

    @Override
    public URoResponse<PbLcdSwitchPage.LcdSwitchPageResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbLcdSwitchPage.LcdSwitchPageResponse data;
        try {
            PbLcdSwitchPage.LcdSwitchPageResponse response = PbLcdSwitchPage.LcdSwitchPageResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbLcdSwitchPage.LcdSwitchPageRequest.Builder builder = PbLcdSwitchPage.LcdSwitchPageRequest.newBuilder();
        builder.setPage(request.getParameter("page", 0));
        return builder.build().toByteArray();
    }

}
