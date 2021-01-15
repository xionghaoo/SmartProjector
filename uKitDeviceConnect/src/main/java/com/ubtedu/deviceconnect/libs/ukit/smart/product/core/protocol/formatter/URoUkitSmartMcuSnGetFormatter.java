package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbMcuSnGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 23
 * @FunctionName mcu_sn_get
 * @Description 获取MCU芯片ID
 * @FileName URoUkitSmartMcuSnGetFormatter.java
 * 
 **/
public class URoUkitSmartMcuSnGetFormatter extends URoUkitSmartCommandFormatter<PbMcuSnGet.McuSnGetResponse> {

    private URoUkitSmartMcuSnGetFormatter(){}

    public static final URoUkitSmartMcuSnGetFormatter INSTANCE = new URoUkitSmartMcuSnGetFormatter();

    @Override
    public URoResponse<PbMcuSnGet.McuSnGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbMcuSnGet.McuSnGetResponse data;
        try {
            PbMcuSnGet.McuSnGetResponse response = PbMcuSnGet.McuSnGetResponse.parseFrom(bizData);
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
