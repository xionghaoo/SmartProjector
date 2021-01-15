package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVoiceGetLanguage;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/12/04
 * @CmdId 1038
 * @FunctionName voice_get_language
 * @Description 获取语言
 * @FileName URoUkitSmartVoiceGetLanguageFormatter.java
 * 
 **/
 public class URoUkitSmartVoiceGetLanguageFormatter extends URoUkitSmartCommandFormatter<PbVoiceGetLanguage.VoiceGetLanguageResponse> {

    private URoUkitSmartVoiceGetLanguageFormatter(){}

    public static final URoUkitSmartVoiceGetLanguageFormatter INSTANCE = new URoUkitSmartVoiceGetLanguageFormatter();

    @Override
    public URoResponse<PbVoiceGetLanguage.VoiceGetLanguageResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVoiceGetLanguage.VoiceGetLanguageResponse data;
        try {
            PbVoiceGetLanguage.VoiceGetLanguageResponse response = PbVoiceGetLanguage.VoiceGetLanguageResponse.parseFrom(bizData);
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
