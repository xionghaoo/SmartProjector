package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLedBeltBrightnessSet;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLedBeltExpressionsSet;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/04/30
 * @CmdId 1003
 * @FunctionName led_belt_brightness_set
 * @Description 灯带亮度设置
 * @FileName URoUkitSmartLedBeltBrightnessSetFormatter.java
 * 
 **/
 public class URoUkitSmartLedBeltBrightnessSetFormatter extends URoUkitSmartCommandFormatter<PbLedBeltBrightnessSet.LedBeltBrightnessSetResponse> {

    private URoUkitSmartLedBeltBrightnessSetFormatter(){}

    public static final URoUkitSmartLedBeltBrightnessSetFormatter INSTANCE = new URoUkitSmartLedBeltBrightnessSetFormatter();

    @Override
    public URoResponse<PbLedBeltBrightnessSet.LedBeltBrightnessSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbLedBeltBrightnessSet.LedBeltBrightnessSetResponse data;
        try {
            PbLedBeltBrightnessSet.LedBeltBrightnessSetResponse response = PbLedBeltBrightnessSet.LedBeltBrightnessSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbLedBeltBrightnessSet.LedBeltBrightnessSetRequest.Builder builder = PbLedBeltBrightnessSet.LedBeltBrightnessSetRequest.newBuilder();
        HashMap<Integer, Integer> colorLump = request.getParameter("brightness", null);
        for(Map.Entry<Integer, Integer> entry : colorLump.entrySet()) {
            PbLedBeltBrightnessSet.LedBeltBrightnessSetRequest.info.Builder brightnessBuilder = PbLedBeltBrightnessSet.LedBeltBrightnessSetRequest.info.newBuilder();
            brightnessBuilder.setPort(entry.getKey());
            brightnessBuilder.setBrightness(entry.getValue());
            builder.addBrightness(brightnessBuilder);
        }
        return builder.build().toByteArray();
    }

}
