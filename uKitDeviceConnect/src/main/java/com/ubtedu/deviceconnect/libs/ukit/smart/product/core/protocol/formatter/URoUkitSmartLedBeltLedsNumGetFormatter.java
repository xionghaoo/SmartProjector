package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLedBeltLedsNumGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/04/30
 * @CmdId 1001
 * @FunctionName led_belt_leds_num_get
 * @Description 获取灯珠数量
 * @FileName URoUkitSmartLedBeltLedsNumGetFormatter.java
 * 
 **/
 public class URoUkitSmartLedBeltLedsNumGetFormatter extends URoUkitSmartCommandFormatter<PbLedBeltLedsNumGet.LedBeltLedsNumGetResponse> {

    private URoUkitSmartLedBeltLedsNumGetFormatter(){}

    public static final URoUkitSmartLedBeltLedsNumGetFormatter INSTANCE = new URoUkitSmartLedBeltLedsNumGetFormatter();

    @Override
    public URoResponse<PbLedBeltLedsNumGet.LedBeltLedsNumGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbLedBeltLedsNumGet.LedBeltLedsNumGetResponse data;
        try {
            PbLedBeltLedsNumGet.LedBeltLedsNumGetResponse response = PbLedBeltLedsNumGet.LedBeltLedsNumGetResponse.parseFrom(bizData);
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
