package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLcdGetPage;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/06/30
 * @CmdId 1012
 * @FunctionName lcd_get_page
 * @Description 获取当前页
 * @FileName URoUkitSmartLcdGetPageFormatter.java
 * 
 **/
 public class URoUkitSmartLcdGetPageFormatter extends URoUkitSmartCommandFormatter<PbLcdGetPage.LcdGetPageResponse> {

    private URoUkitSmartLcdGetPageFormatter(){}

    public static final URoUkitSmartLcdGetPageFormatter INSTANCE = new URoUkitSmartLcdGetPageFormatter();

    @Override
    public URoResponse<PbLcdGetPage.LcdGetPageResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbLcdGetPage.LcdGetPageResponse data;
        try {
            PbLcdGetPage.LcdGetPageResponse response = PbLcdGetPage.LcdGetPageResponse.parseFrom(bizData);
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
