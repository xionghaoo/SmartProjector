package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbFileDownloadProgressReport;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/22
 * @CmdId 257
 * @FunctionName file_download_progress_report
 * @Description 文件下载进度上报
 * @FileName URoUkitSmartFileDownloadProgressReportFormatter.java
 * 
 **/
public class URoUkitSmartFileDownloadProgressReportFormatter extends URoUkitSmartCommandFormatter<PbFileDownloadProgressReport.FileDownloadProgressReportResponse> {

    private URoUkitSmartFileDownloadProgressReportFormatter(){}

    public static final URoUkitSmartFileDownloadProgressReportFormatter INSTANCE = new URoUkitSmartFileDownloadProgressReportFormatter();

    @Override
    public URoResponse<PbFileDownloadProgressReport.FileDownloadProgressReportResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbFileDownloadProgressReport.FileDownloadProgressReportResponse data;
        try {
            PbFileDownloadProgressReport.FileDownloadProgressReportResponse response = PbFileDownloadProgressReport.FileDownloadProgressReportResponse.parseFrom(bizData);
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
