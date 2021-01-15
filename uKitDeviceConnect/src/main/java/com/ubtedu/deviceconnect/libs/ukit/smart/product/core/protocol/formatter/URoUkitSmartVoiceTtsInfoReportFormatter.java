package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVoiceTtsInfoReport;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/12/04
 * @CmdId 1031
 * @FunctionName voice_tts_info_report
 * @Description tts播放结果通知
 * @FileName URoUkitSmartVoiceTtsInfoReportFormatter.java
 * 
 **/
 public class URoUkitSmartVoiceTtsInfoReportFormatter extends URoUkitSmartCommandFormatter<PbVoiceTtsInfoReport.VoiceTtsInfoReportResponse> {

    private URoUkitSmartVoiceTtsInfoReportFormatter(){}

    public static final URoUkitSmartVoiceTtsInfoReportFormatter INSTANCE = new URoUkitSmartVoiceTtsInfoReportFormatter();

    @Override
    public URoResponse<PbVoiceTtsInfoReport.VoiceTtsInfoReportResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVoiceTtsInfoReport.VoiceTtsInfoReportResponse data;
        try {
            PbVoiceTtsInfoReport.VoiceTtsInfoReportResponse response = PbVoiceTtsInfoReport.VoiceTtsInfoReportResponse.parseFrom(bizData);
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
