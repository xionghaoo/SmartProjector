package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVoiceRecordPlayStart;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/12/04
 * @CmdId 1027
 * @FunctionName voice_record_play_start
 * @Description 播放录音文件
 * @FileName URoUkitSmartVoiceRecordPlayStartFormatter.java
 * 
 **/
 public class URoUkitSmartVoiceRecordPlayStartFormatter extends URoUkitSmartCommandFormatter<PbVoiceRecordPlayStart.VoiceRecordPlayStartResponse> {

    private URoUkitSmartVoiceRecordPlayStartFormatter(){}

    public static final URoUkitSmartVoiceRecordPlayStartFormatter INSTANCE = new URoUkitSmartVoiceRecordPlayStartFormatter();

    @Override
    public URoResponse<PbVoiceRecordPlayStart.VoiceRecordPlayStartResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVoiceRecordPlayStart.VoiceRecordPlayStartResponse data;
        try {
            PbVoiceRecordPlayStart.VoiceRecordPlayStartResponse response = PbVoiceRecordPlayStart.VoiceRecordPlayStartResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbVoiceRecordPlayStart.VoiceRecordPlayStartRequest.Builder builder = PbVoiceRecordPlayStart.VoiceRecordPlayStartRequest.newBuilder();
        builder.setFileName(request.getParameter("file_name", ""));
        builder.setSessionId(request.getParameter("session_id", 0));
        builder.setFileFormat(0);
        return builder.build().toByteArray();
    }

}
