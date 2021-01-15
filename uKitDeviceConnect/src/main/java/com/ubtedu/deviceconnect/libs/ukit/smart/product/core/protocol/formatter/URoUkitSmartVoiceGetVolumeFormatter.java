package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVoiceGetVolume;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/12/04
 * @CmdId 1036
 * @FunctionName voice_get_volume
 * @Description 获取音量
 * @FileName URoUkitSmartVoiceGetVolumeFormatter.java
 * 
 **/
 public class URoUkitSmartVoiceGetVolumeFormatter extends URoUkitSmartCommandFormatter<PbVoiceGetVolume.VoiceGetVolumeResponse> {

    private URoUkitSmartVoiceGetVolumeFormatter(){}

    public static final URoUkitSmartVoiceGetVolumeFormatter INSTANCE = new URoUkitSmartVoiceGetVolumeFormatter();

    @Override
    public URoResponse<PbVoiceGetVolume.VoiceGetVolumeResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVoiceGetVolume.VoiceGetVolumeResponse data;
        try {
            PbVoiceGetVolume.VoiceGetVolumeResponse response = PbVoiceGetVolume.VoiceGetVolumeResponse.parseFrom(bizData);
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
