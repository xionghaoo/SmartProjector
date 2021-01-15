package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVoiceRecordCancel;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/04/27
 * @CmdId 1040
 * @FunctionName voice_record_cancel
 * @Description 取消录制语音文件
 * @FileName URoUkitSmartVoiceRecordCancelFormatter.java
 * 
 **/
 public class URoUkitSmartVoiceRecordCancelFormatter extends URoUkitSmartCommandFormatter<PbVoiceRecordCancel.VoiceRecordCancelResponse> {

    private URoUkitSmartVoiceRecordCancelFormatter(){}

    public static final URoUkitSmartVoiceRecordCancelFormatter INSTANCE = new URoUkitSmartVoiceRecordCancelFormatter();

    @Override
    public URoResponse<PbVoiceRecordCancel.VoiceRecordCancelResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVoiceRecordCancel.VoiceRecordCancelResponse data;
        try {
            PbVoiceRecordCancel.VoiceRecordCancelResponse response = PbVoiceRecordCancel.VoiceRecordCancelResponse.parseFrom(bizData);
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
