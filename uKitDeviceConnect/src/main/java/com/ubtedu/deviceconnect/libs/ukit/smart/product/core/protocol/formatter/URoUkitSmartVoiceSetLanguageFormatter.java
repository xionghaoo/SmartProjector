package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVoiceSetLanguage;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/12/04
 * @CmdId 1037
 * @FunctionName voice_set_language
 * @Description 设置语言
 * @FileName URoUkitSmartVoiceSetLanguageFormatter.java
 * 
 **/
 public class URoUkitSmartVoiceSetLanguageFormatter extends URoUkitSmartCommandFormatter<PbVoiceSetLanguage.VoiceSetLanguageResponse> {

    private URoUkitSmartVoiceSetLanguageFormatter(){}

    public static final URoUkitSmartVoiceSetLanguageFormatter INSTANCE = new URoUkitSmartVoiceSetLanguageFormatter();

    @Override
    public URoResponse<PbVoiceSetLanguage.VoiceSetLanguageResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVoiceSetLanguage.VoiceSetLanguageResponse data;
        try {
            PbVoiceSetLanguage.VoiceSetLanguageResponse response = PbVoiceSetLanguage.VoiceSetLanguageResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbVoiceSetLanguage.VoiceSetLanguageRequest.Builder builder = PbVoiceSetLanguage.VoiceSetLanguageRequest.newBuilder();
        builder.setValue(request.getParameter("value", 0));
        return builder.build().toByteArray();
    }

}
