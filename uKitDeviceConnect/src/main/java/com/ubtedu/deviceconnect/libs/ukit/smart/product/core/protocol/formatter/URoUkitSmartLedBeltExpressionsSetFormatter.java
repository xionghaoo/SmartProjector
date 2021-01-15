package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLedBeltExpressionsSet;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/04/30
 * @CmdId 1000
 * @FunctionName led_belt_expressions_set
 * @Description 灯带帧控制
 * @FileName URoUkitSmartLedBeltExpressionsSetFormatter.java
 * 
 **/
 public class URoUkitSmartLedBeltExpressionsSetFormatter extends URoUkitSmartCommandFormatter<PbLedBeltExpressionsSet.LedBeltExpressionsSetResponse> {

    private URoUkitSmartLedBeltExpressionsSetFormatter(){}

    public static final URoUkitSmartLedBeltExpressionsSetFormatter INSTANCE = new URoUkitSmartLedBeltExpressionsSetFormatter();

    @Override
    public URoResponse<PbLedBeltExpressionsSet.LedBeltExpressionsSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbLedBeltExpressionsSet.LedBeltExpressionsSetResponse data;
        try {
            PbLedBeltExpressionsSet.LedBeltExpressionsSetResponse response = PbLedBeltExpressionsSet.LedBeltExpressionsSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbLedBeltExpressionsSet.LedBeltExpressionsSetRequest.Builder builder = PbLedBeltExpressionsSet.LedBeltExpressionsSetRequest.newBuilder();
        builder.setPort(request.getParameter("port", 0));
        HashMap<Integer, Integer> colorLump = request.getParameter("color_lump", null);
        for(Map.Entry<Integer, Integer> entry : colorLump.entrySet()) {
            PbLedBeltExpressionsSet.LedBeltExpressionsSetRequest.dev_color_lump.Builder colorBuilder = PbLedBeltExpressionsSet.LedBeltExpressionsSetRequest.dev_color_lump.newBuilder();
            colorBuilder.setIndex(entry.getKey());
            colorBuilder.setRgbc(entry.getValue());
            builder.addColorLump(colorBuilder);
        }
        return builder.build().toByteArray();
    }

}
