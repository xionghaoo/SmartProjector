package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevRecordingFileReport;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/09/19
 * @CmdId 3000
 * @FunctionName intelligent_dev_recording_file_report
 * @Description 上报录音文件信息
 * @FileName URoUkitSmartIntelligentDevRecordingFileReportFormatter.java
 * 
 **/
 public class URoUkitSmartIntelligentDevRecordingFileReportFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevRecordingFileReport.IntelligentDevRecordingFileReportResponse> {

    private URoUkitSmartIntelligentDevRecordingFileReportFormatter(){}

    public static final URoUkitSmartIntelligentDevRecordingFileReportFormatter INSTANCE = new URoUkitSmartIntelligentDevRecordingFileReportFormatter();

    @Override
    public URoResponse<PbIntelligentDevRecordingFileReport.IntelligentDevRecordingFileReportResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevRecordingFileReport.IntelligentDevRecordingFileReportResponse data;
        try {
            PbIntelligentDevRecordingFileReport.IntelligentDevRecordingFileReportResponse response = PbIntelligentDevRecordingFileReport.IntelligentDevRecordingFileReportResponse.parseFrom(bizData);
            // TODO check the response
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbIntelligentDevRecordingFileReport.IntelligentDevRecordingFileReportRequest.Builder builder = PbIntelligentDevRecordingFileReport.IntelligentDevRecordingFileReportRequest.newBuilder();
        /* TODO set request params to pb
        builder.setPath(request.getParameter("path", ""));
        */
        return builder.build().toByteArray();
    }

}
