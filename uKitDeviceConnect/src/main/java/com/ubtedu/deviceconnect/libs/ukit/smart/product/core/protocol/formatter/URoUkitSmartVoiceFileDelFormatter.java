package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVoiceFileDel;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/12/04
 * @CmdId 1020
 * @FunctionName voice_file_del
 * @Description 删除语音文件
 * @FileName URoUkitSmartVoiceFileDelFormatter.java
 * 
 **/
 public class URoUkitSmartVoiceFileDelFormatter extends URoUkitSmartCommandFormatter<PbVoiceFileDel.VoiceFileDelResponse> {

    private URoUkitSmartVoiceFileDelFormatter(){}

    public static final URoUkitSmartVoiceFileDelFormatter INSTANCE = new URoUkitSmartVoiceFileDelFormatter();

    @Override
    public URoResponse<PbVoiceFileDel.VoiceFileDelResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVoiceFileDel.VoiceFileDelResponse data;
        try {
            PbVoiceFileDel.VoiceFileDelResponse response = PbVoiceFileDel.VoiceFileDelResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbVoiceFileDel.VoiceFileDelRequest.Builder builder = PbVoiceFileDel.VoiceFileDelRequest.newBuilder();
        builder.setFileName(request.getParameter("file_name", ""));
        return builder.build().toByteArray();
    }

}
