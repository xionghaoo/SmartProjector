package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbBatteryCapacity;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1006
 * @FunctionName battery_capacity
 * @Description 获取电池容量
 * @FileName URoUkitSmartBatteryCapacityFormatter.java
 * 
 **/
public class URoUkitSmartBatteryCapacityFormatter extends URoUkitSmartCommandFormatter<PbBatteryCapacity.BatteryCapacityResponse> {

    private URoUkitSmartBatteryCapacityFormatter(){}

    public static final URoUkitSmartBatteryCapacityFormatter INSTANCE = new URoUkitSmartBatteryCapacityFormatter();

    @Override
    public URoResponse<PbBatteryCapacity.BatteryCapacityResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbBatteryCapacity.BatteryCapacityResponse data;
        try {
            PbBatteryCapacity.BatteryCapacityResponse response = PbBatteryCapacity.BatteryCapacityResponse.parseFrom(bizData);
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
