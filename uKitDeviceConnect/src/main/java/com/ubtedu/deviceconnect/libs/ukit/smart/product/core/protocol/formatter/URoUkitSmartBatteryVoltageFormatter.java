package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbBatteryVoltage;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1003
 * @FunctionName battery_voltage
 * @Description 获取电池电压
 * @FileName URoUkitSmartBatteryVoltageFormatter.java
 * 
 **/
public class URoUkitSmartBatteryVoltageFormatter extends URoUkitSmartCommandFormatter<PbBatteryVoltage.BatteryVoltageResponse> {

    private URoUkitSmartBatteryVoltageFormatter(){}

    public static final URoUkitSmartBatteryVoltageFormatter INSTANCE = new URoUkitSmartBatteryVoltageFormatter();

    @Override
    public URoResponse<PbBatteryVoltage.BatteryVoltageResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbBatteryVoltage.BatteryVoltageResponse data;
        try {
            PbBatteryVoltage.BatteryVoltageResponse response = PbBatteryVoltage.BatteryVoltageResponse.parseFrom(bizData);
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
