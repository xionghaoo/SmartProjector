package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbSoundCalibrateGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1003
 * @FunctionName sound_calibrate_get
 * @Description 获取声响参数
 * @FileName URoUkitSmartSoundCalibrateGetFormatter.java
 * 
 **/
public class URoUkitSmartSoundCalibrateGetFormatter extends URoUkitSmartCommandFormatter<PbSoundCalibrateGet.SoundCalibrateGetResponse> {

    private URoUkitSmartSoundCalibrateGetFormatter(){}

    public static final URoUkitSmartSoundCalibrateGetFormatter INSTANCE = new URoUkitSmartSoundCalibrateGetFormatter();

    @Override
    public URoResponse<PbSoundCalibrateGet.SoundCalibrateGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbSoundCalibrateGet.SoundCalibrateGetResponse data;
        try {
            PbSoundCalibrateGet.SoundCalibrateGetResponse response = PbSoundCalibrateGet.SoundCalibrateGetResponse.parseFrom(bizData);
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
