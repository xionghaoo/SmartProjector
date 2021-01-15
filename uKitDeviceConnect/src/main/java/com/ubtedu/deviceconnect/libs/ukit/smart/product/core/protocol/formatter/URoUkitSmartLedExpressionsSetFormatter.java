package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLedExpressionsSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1001
 * @FunctionName led_expressions_set
 * @Description 灯光模块表情帧控制
 * @FileName URoUkitSmartLedExpressionsSetFormatter.java
 * 
 **/
public class URoUkitSmartLedExpressionsSetFormatter extends URoUkitSmartCommandFormatter<PbLedExpressionsSet.LedExpressionsSetResponse> {

    private URoUkitSmartLedExpressionsSetFormatter(){}

    public static final URoUkitSmartLedExpressionsSetFormatter INSTANCE = new URoUkitSmartLedExpressionsSetFormatter();

    @Override
    public URoResponse<PbLedExpressionsSet.LedExpressionsSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbLedExpressionsSet.LedExpressionsSetResponse data;
        try {
            PbLedExpressionsSet.LedExpressionsSetResponse response = PbLedExpressionsSet.LedExpressionsSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbLedExpressionsSet.LedExpressionsSetRequest.Builder builder = PbLedExpressionsSet.LedExpressionsSetRequest.newBuilder();
        int timeInt = request.getParameter("time", 0);
        builder.setTime(timeInt);
        int rgbc = request.getParameter("rgbc", 0);
        for(int i = 0; i < 8; i++) {
            int index = 1 << i;
            String key = "rgbc" + (i + 1);
            PbLedExpressionsSet.LedExpressionsSetRequest.dev_color_lump.Builder colorBuilder = PbLedExpressionsSet.LedExpressionsSetRequest.dev_color_lump.newBuilder();
            colorBuilder.setIndex(index);
            colorBuilder.setRgbc(request.getParameter(key, rgbc));
            builder.addColorLump(colorBuilder);
        }
        return builder.build().toByteArray();
    }

}
