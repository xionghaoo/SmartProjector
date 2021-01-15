package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVoiceAsrInfoReport;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/12/04
 * @CmdId 1034
 * @FunctionName voice_asr_info_report
 * @Description asr结果通知
 * @FileName URoUkitSmartVoiceAsrInfoReportFormatter.java
 * 
 **/
 public class URoUkitSmartVoiceAsrInfoReportFormatter extends URoUkitSmartCommandFormatter<PbVoiceAsrInfoReport.VoiceAsrInfoReportResponse> {

    private URoUkitSmartVoiceAsrInfoReportFormatter(){}

    public static final URoUkitSmartVoiceAsrInfoReportFormatter INSTANCE = new URoUkitSmartVoiceAsrInfoReportFormatter();

    @Override
    public URoResponse<PbVoiceAsrInfoReport.VoiceAsrInfoReportResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVoiceAsrInfoReport.VoiceAsrInfoReportResponse data;
        try {
            PbVoiceAsrInfoReport.VoiceAsrInfoReportResponse response = PbVoiceAsrInfoReport.VoiceAsrInfoReportResponse.parseFrom(bizData);
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
