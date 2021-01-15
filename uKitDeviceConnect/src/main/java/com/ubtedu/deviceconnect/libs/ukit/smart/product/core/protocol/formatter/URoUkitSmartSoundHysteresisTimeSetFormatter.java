package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbSoundHysteresisTimeSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1001
 * @FunctionName sound_hysteresis_time_set
 * @Description 设置声响的有效ADC值迟滞时间
 * @FileName URoUkitSmartSoundHysteresisTimeSetFormatter.java
 * 
 **/
public class URoUkitSmartSoundHysteresisTimeSetFormatter extends URoUkitSmartCommandFormatter<PbSoundHysteresisTimeSet.SoundHysteresisTimeSetResponse> {

    private URoUkitSmartSoundHysteresisTimeSetFormatter(){}

    public static final URoUkitSmartSoundHysteresisTimeSetFormatter INSTANCE = new URoUkitSmartSoundHysteresisTimeSetFormatter();

    @Override
    public URoResponse<PbSoundHysteresisTimeSet.SoundHysteresisTimeSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbSoundHysteresisTimeSet.SoundHysteresisTimeSetResponse data;
        try {
            PbSoundHysteresisTimeSet.SoundHysteresisTimeSetResponse response = PbSoundHysteresisTimeSet.SoundHysteresisTimeSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbSoundHysteresisTimeSet.SoundHysteresisTimeSetRequest.Builder builder = PbSoundHysteresisTimeSet.SoundHysteresisTimeSetRequest.newBuilder();
        builder.setHysteresisTime(request.getParameter("hysteresis_time", 0));
        return builder.build().toByteArray();
    }

}
