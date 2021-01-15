package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLedFixedExpressionsSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1000
 * @FunctionName led_fixed_expressions_set
 * @Description 灯光模块固定表情控制
 * @FileName URoUkitSmartLedFixedExpressionsSetFormatter.java
 * 
 **/
public class URoUkitSmartLedFixedExpressionsSetFormatter extends URoUkitSmartCommandFormatter<PbLedFixedExpressionsSet.LedFixedExpressionsSetResponse> {

    private URoUkitSmartLedFixedExpressionsSetFormatter(){}

    public static final URoUkitSmartLedFixedExpressionsSetFormatter INSTANCE = new URoUkitSmartLedFixedExpressionsSetFormatter();

    @Override
    public URoResponse<PbLedFixedExpressionsSet.LedFixedExpressionsSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbLedFixedExpressionsSet.LedFixedExpressionsSetResponse data;
        try {
            PbLedFixedExpressionsSet.LedFixedExpressionsSetResponse response = PbLedFixedExpressionsSet.LedFixedExpressionsSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbLedFixedExpressionsSet.LedFixedExpressionsSetRequest.Builder builder = PbLedFixedExpressionsSet.LedFixedExpressionsSetRequest.newBuilder();
        builder.setExpressionsType(request.getParameter("expressions_type", 0));
        builder.setTime(request.getParameter("time", 0));
        builder.setRgbc(request.getParameter("rgbc", 0));
        return builder.build().toByteArray();
    }

}
