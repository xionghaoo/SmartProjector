package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbSoundAdcValueGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1000
 * @FunctionName sound_adc_value_get
 * @Description 回读声响ADC值
 * @FileName URoUkitSmartSoundAdcValueGetFormatter.java
 * 
 **/
public class URoUkitSmartSoundAdcValueGetFormatter extends URoUkitSmartCommandFormatter<PbSoundAdcValueGet.SoundAdcValueGetResponse> {

    private URoUkitSmartSoundAdcValueGetFormatter(){}

    public static final URoUkitSmartSoundAdcValueGetFormatter INSTANCE = new URoUkitSmartSoundAdcValueGetFormatter();

    @Override
    public URoResponse<PbSoundAdcValueGet.SoundAdcValueGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbSoundAdcValueGet.SoundAdcValueGetResponse data;
        try {
            PbSoundAdcValueGet.SoundAdcValueGetResponse response = PbSoundAdcValueGet.SoundAdcValueGetResponse.parseFrom(bizData);
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
