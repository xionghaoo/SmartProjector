package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVoiceSetVolume;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/12/04
 * @CmdId 1035
 * @FunctionName voice_set_volume
 * @Description 设置音量
 * @FileName URoUkitSmartVoiceSetVolumeFormatter.java
 * 
 **/
 public class URoUkitSmartVoiceSetVolumeFormatter extends URoUkitSmartCommandFormatter<PbVoiceSetVolume.VoiceSetVolumeResponse> {

    private URoUkitSmartVoiceSetVolumeFormatter(){}

    public static final URoUkitSmartVoiceSetVolumeFormatter INSTANCE = new URoUkitSmartVoiceSetVolumeFormatter();

    @Override
    public URoResponse<PbVoiceSetVolume.VoiceSetVolumeResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVoiceSetVolume.VoiceSetVolumeResponse data;
        try {
            PbVoiceSetVolume.VoiceSetVolumeResponse response = PbVoiceSetVolume.VoiceSetVolumeResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbVoiceSetVolume.VoiceSetVolumeRequest.Builder builder = PbVoiceSetVolume.VoiceSetVolumeRequest.newBuilder();
        builder.setValue(request.getParameter("value", 0));
        return builder.build().toByteArray();
    }

}
