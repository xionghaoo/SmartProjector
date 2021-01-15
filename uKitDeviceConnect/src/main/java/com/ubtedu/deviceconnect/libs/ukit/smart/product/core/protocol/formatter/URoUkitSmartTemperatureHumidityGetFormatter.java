package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbTemperatureHumidityGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1000
 * @FunctionName temperature_humidity_get
 * @Description 回读温湿度值
 * @FileName URoUkitSmartTemperatureHumidityGetFormatter.java
 * 
 **/
public class URoUkitSmartTemperatureHumidityGetFormatter extends URoUkitSmartCommandFormatter<PbTemperatureHumidityGet.TemperatureHumidityGetResponse> {

    private URoUkitSmartTemperatureHumidityGetFormatter(){}

    public static final URoUkitSmartTemperatureHumidityGetFormatter INSTANCE = new URoUkitSmartTemperatureHumidityGetFormatter();

    @Override
    public URoResponse<PbTemperatureHumidityGet.TemperatureHumidityGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbTemperatureHumidityGet.TemperatureHumidityGetResponse data;
        try {
            PbTemperatureHumidityGet.TemperatureHumidityGetResponse response = PbTemperatureHumidityGet.TemperatureHumidityGetResponse.parseFrom(bizData);
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
