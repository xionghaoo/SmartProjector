package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVoicePlayInfoReport;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/12/04
 * @CmdId 1029
 * @FunctionName voice_play_info_report
 * @Description 播放结果通知
 * @FileName URoUkitSmartVoicePlayInfoReportFormatter.java
 * 
 **/
 public class URoUkitSmartVoicePlayInfoReportFormatter extends URoUkitSmartCommandFormatter<PbVoicePlayInfoReport.VoicePlayInfoReportResponse> {

    private URoUkitSmartVoicePlayInfoReportFormatter(){}

    public static final URoUkitSmartVoicePlayInfoReportFormatter INSTANCE = new URoUkitSmartVoicePlayInfoReportFormatter();

    @Override
    public URoResponse<PbVoicePlayInfoReport.VoicePlayInfoReportResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVoicePlayInfoReport.VoicePlayInfoReportResponse data;
        try {
            PbVoicePlayInfoReport.VoicePlayInfoReportResponse response = PbVoicePlayInfoReport.VoicePlayInfoReportResponse.parseFrom(bizData);
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
