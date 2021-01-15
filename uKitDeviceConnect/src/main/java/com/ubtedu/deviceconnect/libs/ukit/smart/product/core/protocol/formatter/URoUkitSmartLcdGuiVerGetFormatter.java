package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLcdGuiVerGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/06/22
 * @CmdId 1000
 * @FunctionName lcd_gui_ver_get
 * @Description 获取GUI版本号
 * @FileName URoUkitSmartLcdGuiVerGetFormatter.java
 * 
 **/
 public class URoUkitSmartLcdGuiVerGetFormatter extends URoUkitSmartCommandFormatter<PbLcdGuiVerGet.LcdGuiVerGetResponse> {

    private URoUkitSmartLcdGuiVerGetFormatter(){}

    public static final URoUkitSmartLcdGuiVerGetFormatter INSTANCE = new URoUkitSmartLcdGuiVerGetFormatter();

    @Override
    public URoResponse<PbLcdGuiVerGet.LcdGuiVerGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbLcdGuiVerGet.LcdGuiVerGetResponse data;
        try {
            PbLcdGuiVerGet.LcdGuiVerGetResponse response = PbLcdGuiVerGet.LcdGuiVerGetResponse.parseFrom(bizData);
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
