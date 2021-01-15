package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVoiceStartRecord;

/**
 *
 * NOTICE: This file is generation by script.
 *
 * @Author naOKi
 * @Date 2019/12/04
 * @CmdId 1023
 * @FunctionName voice_start_record
 * @Description 开始录制语音文件
 * @FileName URoUkitSmartVoiceStartRecordFormatter.java
 *
 **/
public class URoUkitSmartVoiceStartRecordFormatter extends URoUkitSmartCommandFormatter<PbVoiceStartRecord.VoiceStartRecordResponse> {

    private URoUkitSmartVoiceStartRecordFormatter(){}

    public static final URoUkitSmartVoiceStartRecordFormatter INSTANCE = new URoUkitSmartVoiceStartRecordFormatter();

    @Override
    public URoResponse<PbVoiceStartRecord.VoiceStartRecordResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVoiceStartRecord.VoiceStartRecordResponse data;
        try {
            PbVoiceStartRecord.VoiceStartRecordResponse response = PbVoiceStartRecord.VoiceStartRecordResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbVoiceStartRecord.VoiceStartRecordRequest.Builder builder = PbVoiceStartRecord.VoiceStartRecordRequest.newBuilder();
        long duration = request.getParameter("file_duration", 0L);
        builder.setFileName(request.getParameter("file_name", ""));
        builder.setFileDuration((int)duration);
        builder.setSessionId(request.getParameter("session_id", 0));
        builder.setFileFormat(0);
        return builder.build().toByteArray();
    }

}
