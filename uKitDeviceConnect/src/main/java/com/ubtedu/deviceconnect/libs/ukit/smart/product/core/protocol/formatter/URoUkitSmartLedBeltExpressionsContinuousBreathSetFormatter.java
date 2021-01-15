package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLedBeltExpressionsContinuousBreathSet;

import java.util.HashMap;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/04/30
 * @CmdId 1007
 * @FunctionName led_belt_expressions_continuous_breath_set
 * @Description 灯带连续灯珠呼吸设置
 * @FileName URoUkitSmartLedBeltExpressionsContinuousBreathSetFormatter.java
 * 
 **/
 public class URoUkitSmartLedBeltExpressionsContinuousBreathSetFormatter extends URoUkitSmartCommandFormatter<PbLedBeltExpressionsContinuousBreathSet.LedBeltExpressionsContinuousBreathSetResponse> {

    private URoUkitSmartLedBeltExpressionsContinuousBreathSetFormatter(){}

    public static final URoUkitSmartLedBeltExpressionsContinuousBreathSetFormatter INSTANCE = new URoUkitSmartLedBeltExpressionsContinuousBreathSetFormatter();

    @Override
    public URoResponse<PbLedBeltExpressionsContinuousBreathSet.LedBeltExpressionsContinuousBreathSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbLedBeltExpressionsContinuousBreathSet.LedBeltExpressionsContinuousBreathSetResponse data;
        try {
            PbLedBeltExpressionsContinuousBreathSet.LedBeltExpressionsContinuousBreathSetResponse response = PbLedBeltExpressionsContinuousBreathSet.LedBeltExpressionsContinuousBreathSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbLedBeltExpressionsContinuousBreathSet.LedBeltExpressionsContinuousBreathSetRequest.Builder builder = PbLedBeltExpressionsContinuousBreathSet.LedBeltExpressionsContinuousBreathSetRequest.newBuilder();
        for(int i = 0; i < 4; i++) {
            String key = "port" + i;
            HashMap<String, Integer> info = request.getParameter(key, null);
            if(info == null) {
                continue;
            }
            if(info.get("port") == null
                    || info.get("rgbc") == null
                    || info.get("start_pixel") == null
                    || info.get("end_pixel") == null
                    || info.get("time") == null) {
                continue;
            }
            PbLedBeltExpressionsContinuousBreathSet.LedBeltExpressionsContinuousBreathSetRequest.info.Builder infoBuilder = PbLedBeltExpressionsContinuousBreathSet.LedBeltExpressionsContinuousBreathSetRequest.info.newBuilder();
            infoBuilder.setPort(info.get("port"));
            infoBuilder.setRgbc(info.get("rgbc"));
            infoBuilder.setStartPixel(info.get("start_pixel"));
            infoBuilder.setEndPixel(info.get("end_pixel"));
            infoBuilder.setTime(info.get("time"));
            builder.addPixel(infoBuilder);
        }
        return builder.build().toByteArray();
    }

}
