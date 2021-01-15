package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLedBeltExpressionsContinuousSet;

import java.util.HashMap;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/04/30
 * @CmdId 1006
 * @FunctionName led_belt_expressions_continuous_set
 * @Description 灯带连续灯珠显示设置
 * @FileName URoUkitSmartLedBeltExpressionsContinuousSetFormatter.java
 * 
 **/
 public class URoUkitSmartLedBeltExpressionsContinuousSetFormatter extends URoUkitSmartCommandFormatter<PbLedBeltExpressionsContinuousSet.LedBeltExpressionsContinuousSetResponse> {

    private URoUkitSmartLedBeltExpressionsContinuousSetFormatter(){}

    public static final URoUkitSmartLedBeltExpressionsContinuousSetFormatter INSTANCE = new URoUkitSmartLedBeltExpressionsContinuousSetFormatter();

    @Override
    public URoResponse<PbLedBeltExpressionsContinuousSet.LedBeltExpressionsContinuousSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbLedBeltExpressionsContinuousSet.LedBeltExpressionsContinuousSetResponse data;
        try {
            PbLedBeltExpressionsContinuousSet.LedBeltExpressionsContinuousSetResponse response = PbLedBeltExpressionsContinuousSet.LedBeltExpressionsContinuousSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbLedBeltExpressionsContinuousSet.LedBeltExpressionsContinuousSetRequest.Builder builder = PbLedBeltExpressionsContinuousSet.LedBeltExpressionsContinuousSetRequest.newBuilder();
        for(int i = 0; i < 4; i++) {
            String key = "port" + i;
            HashMap<String, Integer> info = request.getParameter(key, null);
            if(info == null) {
                continue;
            }
            if(info.get("port") == null || info.get("rgbc") == null || info.get("start_pixel") == null|| info.get("end_pixel") == null) {
                continue;
            }
            PbLedBeltExpressionsContinuousSet.LedBeltExpressionsContinuousSetRequest.info.Builder infoBuilder = PbLedBeltExpressionsContinuousSet.LedBeltExpressionsContinuousSetRequest.info.newBuilder();
            infoBuilder.setPort(info.get("port"));
            infoBuilder.setRgbc(info.get("rgbc"));
            infoBuilder.setStartPixel(info.get("start_pixel"));
            infoBuilder.setEndPixel(info.get("end_pixel"));
            builder.addPixel(infoBuilder);
        }
        return builder.build().toByteArray();
    }

}
