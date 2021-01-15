package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbBatteryPowerPercent;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1000
 * @FunctionName battery_power_percent
 * @Description 获取电池电量百分比
 * @FileName URoUkitSmartBatteryPowerPercentFormatter.java
 * 
 **/
public class URoUkitSmartBatteryPowerPercentFormatter extends URoUkitSmartCommandFormatter<PbBatteryPowerPercent.BatteryPowerPercentResponse> {

    private URoUkitSmartBatteryPowerPercentFormatter(){}

    public static final URoUkitSmartBatteryPowerPercentFormatter INSTANCE = new URoUkitSmartBatteryPowerPercentFormatter();

    @Override
    public URoResponse<PbBatteryPowerPercent.BatteryPowerPercentResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbBatteryPowerPercent.BatteryPowerPercentResponse data;
        try {
            PbBatteryPowerPercent.BatteryPowerPercentResponse response = PbBatteryPowerPercent.BatteryPowerPercentResponse.parseFrom(bizData);
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
