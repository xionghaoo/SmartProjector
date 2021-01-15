package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLcdClear;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/06/22
 * @CmdId 1001
 * @FunctionName lcd_clear
 * @Description 清屏
 * @FileName URoUkitSmartLcdClearFormatter.java
 * 
 **/
 public class URoUkitSmartLcdClearFormatter extends URoUkitSmartCommandFormatter<PbLcdClear.LcdClearResponse> {

    private URoUkitSmartLcdClearFormatter(){}

    public static final URoUkitSmartLcdClearFormatter INSTANCE = new URoUkitSmartLcdClearFormatter();

    @Override
    public URoResponse<PbLcdClear.LcdClearResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbLcdClear.LcdClearResponse data;
        try {
            PbLcdClear.LcdClearResponse response = PbLcdClear.LcdClearResponse.parseFrom(bizData);
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
