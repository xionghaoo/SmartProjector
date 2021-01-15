package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbBatteryChargingCurrent;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1004
 * @FunctionName battery_charging_current
 * @Description 获取电池充电电流
 * @FileName URoUkitSmartBatteryChargingCurrentFormatter.java
 * 
 **/
public class URoUkitSmartBatteryChargingCurrentFormatter extends URoUkitSmartCommandFormatter<PbBatteryChargingCurrent.BatteryChargingCurrentResponse> {

    private URoUkitSmartBatteryChargingCurrentFormatter(){}

    public static final URoUkitSmartBatteryChargingCurrentFormatter INSTANCE = new URoUkitSmartBatteryChargingCurrentFormatter();

    @Override
    public URoResponse<PbBatteryChargingCurrent.BatteryChargingCurrentResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbBatteryChargingCurrent.BatteryChargingCurrentResponse data;
        try {
            PbBatteryChargingCurrent.BatteryChargingCurrentResponse response = PbBatteryChargingCurrent.BatteryChargingCurrentResponse.parseFrom(bizData);
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
