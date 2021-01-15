package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVoiceTtsPlay;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/12/04
 * @CmdId 1030
 * @FunctionName voice_tts_play
 * @Description tts播放
 * @FileName URoUkitSmartVoiceTtsPlayFormatter.java
 * 
 **/
 public class URoUkitSmartVoiceTtsPlayFormatter extends URoUkitSmartCommandFormatter<PbVoiceTtsPlay.VoiceTtsPlayResponse> {

    private URoUkitSmartVoiceTtsPlayFormatter(){}

    public static final URoUkitSmartVoiceTtsPlayFormatter INSTANCE = new URoUkitSmartVoiceTtsPlayFormatter();

    @Override
    public URoResponse<PbVoiceTtsPlay.VoiceTtsPlayResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVoiceTtsPlay.VoiceTtsPlayResponse data;
        try {
            PbVoiceTtsPlay.VoiceTtsPlayResponse response = PbVoiceTtsPlay.VoiceTtsPlayResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbVoiceTtsPlay.VoiceTtsPlayRequest.Builder builder = PbVoiceTtsPlay.VoiceTtsPlayRequest.newBuilder();
        builder.setSessionId(request.getParameter("session_id", 0));
        builder.setText(request.getParameter("text", ""));
        builder.setLanguage(request.getParameter("language", ""));
        return builder.build().toByteArray();
    }

}
