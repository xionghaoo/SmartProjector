package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbBatteryTemperature;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1002
 * @FunctionName battery_temperature
 * @Description 获取电池温度
 * @FileName URoUkitSmartBatteryTemperatureFormatter.java
 * 
 **/
public class URoUkitSmartBatteryTemperatureFormatter extends URoUkitSmartCommandFormatter<PbBatteryTemperature.BatteryTemperatureResponse> {

    private URoUkitSmartBatteryTemperatureFormatter(){}

    public static final URoUkitSmartBatteryTemperatureFormatter INSTANCE = new URoUkitSmartBatteryTemperatureFormatter();

    @Override
    public URoResponse<PbBatteryTemperature.BatteryTemperatureResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbBatteryTemperature.BatteryTemperatureResponse data;
        try {
            PbBatteryTemperature.BatteryTemperatureResponse response = PbBatteryTemperature.BatteryTemperatureResponse.parseFrom(bizData);
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
