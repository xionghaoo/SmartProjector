package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbSoundCalibrateSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1003
 * @FunctionName sound_calibrate_set
 * @Description 设置声响参数
 * @FileName URoUkitSmartSoundCalibrateSetFormatter.java
 * 
 **/
public class URoUkitSmartSoundCalibrateSetFormatter extends URoUkitSmartCommandFormatter<PbSoundCalibrateSet.SoundCalibrateSetResponse> {

    private URoUkitSmartSoundCalibrateSetFormatter(){}

    public static final URoUkitSmartSoundCalibrateSetFormatter INSTANCE = new URoUkitSmartSoundCalibrateSetFormatter();

    @Override
    public URoResponse<PbSoundCalibrateSet.SoundCalibrateSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbSoundCalibrateSet.SoundCalibrateSetResponse data;
        try {
            PbSoundCalibrateSet.SoundCalibrateSetResponse response = PbSoundCalibrateSet.SoundCalibrateSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbSoundCalibrateSet.SoundCalibrateSetRequest.Builder builder = PbSoundCalibrateSet.SoundCalibrateSetRequest.newBuilder();
        builder.setAdcValue(request.getParameter("adc_value", 0));
        builder.setHysteresisTime(request.getParameter("hysteresis_time", 0));
        return builder.build().toByteArray();
    }

}
