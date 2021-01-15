package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbBatteryStatusGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1001
 * @FunctionName battery_status_get
 * @Description 获取电池状态
 * @FileName URoUkitSmartBatteryStatusGetFormatter.java
 * 
 **/
public class URoUkitSmartBatteryStatusGetFormatter extends URoUkitSmartCommandFormatter<PbBatteryStatusGet.BatteryStatusGetResponse> {

    private URoUkitSmartBatteryStatusGetFormatter(){}

    public static final URoUkitSmartBatteryStatusGetFormatter INSTANCE = new URoUkitSmartBatteryStatusGetFormatter();

    @Override
    public URoResponse<PbBatteryStatusGet.BatteryStatusGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbBatteryStatusGet.BatteryStatusGetResponse data;
        try {
            PbBatteryStatusGet.BatteryStatusGetResponse response = PbBatteryStatusGet.BatteryStatusGetResponse.parseFrom(bizData);
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
