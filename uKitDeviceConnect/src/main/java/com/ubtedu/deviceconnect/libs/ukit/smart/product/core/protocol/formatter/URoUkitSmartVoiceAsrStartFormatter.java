package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVoiceAsrStart;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/12/04
 * @CmdId 1032
 * @FunctionName voice_asr_start
 * @Description 启动ASR
 * @FileName URoUkitSmartVoiceAsrStartFormatter.java
 * 
 **/
 public class URoUkitSmartVoiceAsrStartFormatter extends URoUkitSmartCommandFormatter<PbVoiceAsrStart.VoiceAsrStartResponse> {

    private URoUkitSmartVoiceAsrStartFormatter(){}

    public static final URoUkitSmartVoiceAsrStartFormatter INSTANCE = new URoUkitSmartVoiceAsrStartFormatter();

    @Override
    public URoResponse<PbVoiceAsrStart.VoiceAsrStartResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVoiceAsrStart.VoiceAsrStartResponse data;
        try {
            PbVoiceAsrStart.VoiceAsrStartResponse response = PbVoiceAsrStart.VoiceAsrStartResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        String[] keyWords = request.getParameter("key_word", new String[0]);
        PbVoiceAsrStart.VoiceAsrStartRequest.Builder builder = PbVoiceAsrStart.VoiceAsrStartRequest.newBuilder();
        builder.setLanguage(request.getParameter("language", ""));
        for(String keyWord : keyWords) {
            builder.addKeyWord(keyWord);
        }
        return builder.build().toByteArray();
    }

}
