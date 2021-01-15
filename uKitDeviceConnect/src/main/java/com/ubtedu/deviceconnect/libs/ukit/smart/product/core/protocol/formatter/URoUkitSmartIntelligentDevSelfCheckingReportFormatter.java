package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevSelfCheckingReport;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/09/19
 * @CmdId 2503
 * @FunctionName intelligent_dev_self_checking_report
 * @Description 自检信息上报
 * @FileName URoUkitSmartIntelligentDevSelfCheckingReportFormatter.java
 * 
 **/
 public class URoUkitSmartIntelligentDevSelfCheckingReportFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevSelfCheckingReport.IntelligentDevSelfCheckingReportResponse> {

    private URoUkitSmartIntelligentDevSelfCheckingReportFormatter(){}

    public static final URoUkitSmartIntelligentDevSelfCheckingReportFormatter INSTANCE = new URoUkitSmartIntelligentDevSelfCheckingReportFormatter();

    @Override
    public URoResponse<PbIntelligentDevSelfCheckingReport.IntelligentDevSelfCheckingReportResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevSelfCheckingReport.IntelligentDevSelfCheckingReportResponse data;
        try {
            PbIntelligentDevSelfCheckingReport.IntelligentDevSelfCheckingReportResponse response = PbIntelligentDevSelfCheckingReport.IntelligentDevSelfCheckingReportResponse.parseFrom(bizData);
            // TODO check the response
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbIntelligentDevSelfCheckingReport.IntelligentDevSelfCheckingReportRequest.Builder builder = PbIntelligentDevSelfCheckingReport.IntelligentDevSelfCheckingReportRequest.newBuilder();
        /* TODO set request params to pb
        builder.setPath(request.getParameter("path", ""));
        */
        return builder.build().toByteArray();
    }

}
