package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbTemperatureHumidityPush;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/10/24
 * @CmdId 1001
 * @FunctionName temperature_humidity_push
 * @Description 回读温湿度值上报
 * @FileName URoUkitSmartTemperatureHumidityPushFormatter.java
 * 
 **/
 public class URoUkitSmartTemperatureHumidityPushFormatter extends URoUkitSmartCommandFormatter<PbTemperatureHumidityPush.TemperatureHumidityPushResponse> {

    private URoUkitSmartTemperatureHumidityPushFormatter(){}

    public static final URoUkitSmartTemperatureHumidityPushFormatter INSTANCE = new URoUkitSmartTemperatureHumidityPushFormatter();

    @Override
    public URoResponse<PbTemperatureHumidityPush.TemperatureHumidityPushResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbTemperatureHumidityPush.TemperatureHumidityPushResponse data;
        try {
            PbTemperatureHumidityPush.TemperatureHumidityPushResponse response = PbTemperatureHumidityPush.TemperatureHumidityPushResponse.parseFrom(bizData);
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
