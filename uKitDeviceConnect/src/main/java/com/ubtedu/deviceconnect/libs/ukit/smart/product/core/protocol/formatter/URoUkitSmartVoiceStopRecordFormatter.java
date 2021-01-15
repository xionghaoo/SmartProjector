package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVoiceStopRecord;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/12/04
 * @CmdId 1024
 * @FunctionName voice_stop_record
 * @Description 停止录制语音文件
 * @FileName URoUkitSmartVoiceStopRecordFormatter.java
 * 
 **/
 public class URoUkitSmartVoiceStopRecordFormatter extends URoUkitSmartCommandFormatter<PbVoiceStopRecord.VoiceStopRecordResponse> {

    private URoUkitSmartVoiceStopRecordFormatter(){}

    public static final URoUkitSmartVoiceStopRecordFormatter INSTANCE = new URoUkitSmartVoiceStopRecordFormatter();

    @Override
    public URoResponse<PbVoiceStopRecord.VoiceStopRecordResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVoiceStopRecord.VoiceStopRecordResponse data;
        try {
            PbVoiceStopRecord.VoiceStopRecordResponse response = PbVoiceStopRecord.VoiceStopRecordResponse.parseFrom(bizData);
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
