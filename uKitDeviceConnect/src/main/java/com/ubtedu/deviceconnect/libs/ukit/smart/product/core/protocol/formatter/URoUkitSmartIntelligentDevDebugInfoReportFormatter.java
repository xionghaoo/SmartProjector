package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevDebugInfoReport;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/01/13
 * @CmdId 2527
 * @FunctionName intelligent_dev_debug_info_report
 * @Description 上传调试信息
 * @FileName URoUkitSmartIntelligentDevDebugInfoReportFormatter.java
 * 
 **/
 public class URoUkitSmartIntelligentDevDebugInfoReportFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevDebugInfoReport.IntelligentDevDebugInfoReportResponse> {

    private URoUkitSmartIntelligentDevDebugInfoReportFormatter(){}

    public static final URoUkitSmartIntelligentDevDebugInfoReportFormatter INSTANCE = new URoUkitSmartIntelligentDevDebugInfoReportFormatter();

    @Override
    public URoResponse<PbIntelligentDevDebugInfoReport.IntelligentDevDebugInfoReportResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevDebugInfoReport.IntelligentDevDebugInfoReportResponse data;
        try {
            PbIntelligentDevDebugInfoReport.IntelligentDevDebugInfoReportResponse response = PbIntelligentDevDebugInfoReport.IntelligentDevDebugInfoReportResponse.parseFrom(bizData);
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
