package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLedBeltFixedExpressionsSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/04/30
 * @CmdId 1002
 * @FunctionName led_belt_fixed_expressions_set
 * @Description 灯带模块固定表情控制
 * @FileName URoUkitSmartLedBeltFixedExpressionsSetFormatter.java
 * 
 **/
 public class URoUkitSmartLedBeltFixedExpressionsSetFormatter extends URoUkitSmartCommandFormatter<PbLedBeltFixedExpressionsSet.LedBeltFixedExpressionsSetResponse> {

    private URoUkitSmartLedBeltFixedExpressionsSetFormatter(){}

    public static final URoUkitSmartLedBeltFixedExpressionsSetFormatter INSTANCE = new URoUkitSmartLedBeltFixedExpressionsSetFormatter();

    @Override
    public URoResponse<PbLedBeltFixedExpressionsSet.LedBeltFixedExpressionsSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbLedBeltFixedExpressionsSet.LedBeltFixedExpressionsSetResponse data;
        try {
            PbLedBeltFixedExpressionsSet.LedBeltFixedExpressionsSetResponse response = PbLedBeltFixedExpressionsSet.LedBeltFixedExpressionsSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbLedBeltFixedExpressionsSet.LedBeltFixedExpressionsSetRequest.Builder builder = PbLedBeltFixedExpressionsSet.LedBeltFixedExpressionsSetRequest.newBuilder();
        builder.setExpressionsType(request.getParameter("expressions_type", 0));
        builder.setTime(request.getParameter("time", 0));
        builder.setRgbc(request.getParameter("rgbc", 0));
        builder.setPort(request.getParameter("port", 0));
        return builder.build().toByteArray();
    }

}
