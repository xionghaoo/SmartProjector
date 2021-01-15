package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbBatteryDischargeCurrent;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1005
 * @FunctionName battery_discharge_current
 * @Description 获取电池放电电流
 * @FileName URoUkitSmartBatteryDischargeCurrentFormatter.java
 * 
 **/
public class URoUkitSmartBatteryDischargeCurrentFormatter extends URoUkitSmartCommandFormatter<PbBatteryDischargeCurrent.BatteryDischargeCurrentResponse> {

    private URoUkitSmartBatteryDischargeCurrentFormatter(){}

    public static final URoUkitSmartBatteryDischargeCurrentFormatter INSTANCE = new URoUkitSmartBatteryDischargeCurrentFormatter();

    @Override
    public URoResponse<PbBatteryDischargeCurrent.BatteryDischargeCurrentResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbBatteryDischargeCurrent.BatteryDischargeCurrentResponse data;
        try {
            PbBatteryDischargeCurrent.BatteryDischargeCurrentResponse response = PbBatteryDischargeCurrent.BatteryDischargeCurrentResponse.parseFrom(bizData);
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
