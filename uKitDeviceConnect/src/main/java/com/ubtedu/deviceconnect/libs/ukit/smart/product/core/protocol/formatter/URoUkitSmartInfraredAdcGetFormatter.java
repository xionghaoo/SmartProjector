package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbInfraredAdcGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1000
 * @FunctionName infrared_adc_get
 * @Description 读取红外adc值
 * @FileName URoUkitSmartInfraredAdcGetFormatter.java
 * 
 **/
public class URoUkitSmartInfraredAdcGetFormatter extends URoUkitSmartCommandFormatter<PbInfraredAdcGet.InfraredAdcGetResponse> {

    private URoUkitSmartInfraredAdcGetFormatter(){}

    public static final URoUkitSmartInfraredAdcGetFormatter INSTANCE = new URoUkitSmartInfraredAdcGetFormatter();

    @Override
    public URoResponse<PbInfraredAdcGet.InfraredAdcGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbInfraredAdcGet.InfraredAdcGetResponse data;
        try {
            PbInfraredAdcGet.InfraredAdcGetResponse response = PbInfraredAdcGet.InfraredAdcGetResponse.parseFrom(bizData);
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
