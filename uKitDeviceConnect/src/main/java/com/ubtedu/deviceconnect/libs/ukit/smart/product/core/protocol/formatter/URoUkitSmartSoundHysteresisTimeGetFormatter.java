package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbSoundHysteresisTimeGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1002
 * @FunctionName sound_hysteresis_time_get
 * @Description 回读声响的有效ADC值迟滞时间
 * @FileName URoUkitSmartSoundHysteresisTimeGetFormatter.java
 * 
 **/
public class URoUkitSmartSoundHysteresisTimeGetFormatter extends URoUkitSmartCommandFormatter<PbSoundHysteresisTimeGet.SoundHysteresisTimeGetResponse> {

    private URoUkitSmartSoundHysteresisTimeGetFormatter(){}

    public static final URoUkitSmartSoundHysteresisTimeGetFormatter INSTANCE = new URoUkitSmartSoundHysteresisTimeGetFormatter();

    @Override
    public URoResponse<PbSoundHysteresisTimeGet.SoundHysteresisTimeGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbSoundHysteresisTimeGet.SoundHysteresisTimeGetResponse data;
        try {
            PbSoundHysteresisTimeGet.SoundHysteresisTimeGetResponse response = PbSoundHysteresisTimeGet.SoundHysteresisTimeGetResponse.parseFrom(bizData);
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
