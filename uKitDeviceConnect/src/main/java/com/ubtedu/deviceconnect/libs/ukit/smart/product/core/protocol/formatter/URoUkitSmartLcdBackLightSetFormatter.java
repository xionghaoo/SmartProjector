package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLcdBackLightSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/06/22
 * @CmdId 1002
 * @FunctionName lcd_back_light_set
 * @Description 设置背光
 * @FileName URoUkitSmartLcdBackLightSetFormatter.java
 * 
 **/
 public class URoUkitSmartLcdBackLightSetFormatter extends URoUkitSmartCommandFormatter<PbLcdBackLightSet.LcdBackLightSetResponse> {

    private URoUkitSmartLcdBackLightSetFormatter(){}

    public static final URoUkitSmartLcdBackLightSetFormatter INSTANCE = new URoUkitSmartLcdBackLightSetFormatter();

    @Override
    public URoResponse<PbLcdBackLightSet.LcdBackLightSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbLcdBackLightSet.LcdBackLightSetResponse data;
        try {
            PbLcdBackLightSet.LcdBackLightSetResponse response = PbLcdBackLightSet.LcdBackLightSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbLcdBackLightSet.LcdBackLightSetRequest.Builder builder = PbLcdBackLightSet.LcdBackLightSetRequest.newBuilder();
        builder.setValue(request.getParameter("value", 0));
        return builder.build().toByteArray();
    }

}
