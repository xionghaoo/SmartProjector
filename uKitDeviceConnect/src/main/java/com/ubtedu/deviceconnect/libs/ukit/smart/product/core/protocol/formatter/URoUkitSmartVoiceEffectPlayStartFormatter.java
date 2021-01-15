package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVoiceEffectPlayStart;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/12/04
 * @CmdId 1026
 * @FunctionName voice_effect_play_start
 * @Description 播放音效文件
 * @FileName URoUkitSmartVoiceEffectPlayStartFormatter.java
 * 
 **/
 public class URoUkitSmartVoiceEffectPlayStartFormatter extends URoUkitSmartCommandFormatter<PbVoiceEffectPlayStart.VoiceEffectPlayStartResponse> {

    private URoUkitSmartVoiceEffectPlayStartFormatter(){}

    public static final URoUkitSmartVoiceEffectPlayStartFormatter INSTANCE = new URoUkitSmartVoiceEffectPlayStartFormatter();

    @Override
    public URoResponse<PbVoiceEffectPlayStart.VoiceEffectPlayStartResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVoiceEffectPlayStart.VoiceEffectPlayStartResponse data;
        try {
            PbVoiceEffectPlayStart.VoiceEffectPlayStartResponse response = PbVoiceEffectPlayStart.VoiceEffectPlayStartResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbVoiceEffectPlayStart.VoiceEffectPlayStartRequest.Builder builder = PbVoiceEffectPlayStart.VoiceEffectPlayStartRequest.newBuilder();
        builder.setFileName(request.getParameter("file_name", ""));
        builder.setFileType(request.getParameter("file_type", ""));
        builder.setSessionId(request.getParameter("session_id", 0));
        builder.setFileFormat(0);
        return builder.build().toByteArray();
    }

}
