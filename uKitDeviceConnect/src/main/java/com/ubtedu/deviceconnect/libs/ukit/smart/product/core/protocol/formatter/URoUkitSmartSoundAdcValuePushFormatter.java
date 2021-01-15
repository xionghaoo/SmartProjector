package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbSoundAdcValuePush;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/10/24
 * @CmdId 1005
 * @FunctionName sound_adc_value_push
 * @Description 回读声响ADC值
 * @FileName URoUkitSmartSoundAdcValuePushFormatter.java
 * 
 **/
 public class URoUkitSmartSoundAdcValuePushFormatter extends URoUkitSmartCommandFormatter<PbSoundAdcValuePush.SoundAdcValuePushResponse> {

    private URoUkitSmartSoundAdcValuePushFormatter(){}

    public static final URoUkitSmartSoundAdcValuePushFormatter INSTANCE = new URoUkitSmartSoundAdcValuePushFormatter();

    @Override
    public URoResponse<PbSoundAdcValuePush.SoundAdcValuePushResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbSoundAdcValuePush.SoundAdcValuePushResponse data;
        try {
            PbSoundAdcValuePush.SoundAdcValuePushResponse response = PbSoundAdcValuePush.SoundAdcValuePushResponse.parseFrom(bizData);
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
