package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVoiceRecordInfoReport;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/12/04
 * @CmdId 1025
 * @FunctionName voice_record_info_report
 * @Description 录音结果通知
 * @FileName URoUkitSmartVoiceRecordInfoReportFormatter.java
 * 
 **/
 public class URoUkitSmartVoiceRecordInfoReportFormatter extends URoUkitSmartCommandFormatter<PbVoiceRecordInfoReport.VoiceRecordInfoReportResponse> {

    private URoUkitSmartVoiceRecordInfoReportFormatter(){}

    public static final URoUkitSmartVoiceRecordInfoReportFormatter INSTANCE = new URoUkitSmartVoiceRecordInfoReportFormatter();

    @Override
    public URoResponse<PbVoiceRecordInfoReport.VoiceRecordInfoReportResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVoiceRecordInfoReport.VoiceRecordInfoReportResponse data;
        try {
            PbVoiceRecordInfoReport.VoiceRecordInfoReportResponse response = PbVoiceRecordInfoReport.VoiceRecordInfoReportResponse.parseFrom(bizData);
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
