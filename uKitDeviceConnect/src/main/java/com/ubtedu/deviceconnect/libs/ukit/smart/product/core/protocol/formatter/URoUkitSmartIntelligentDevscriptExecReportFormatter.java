package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevscriptExecReport;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/22
 * @CmdId 2002
 * @FunctionName intelligent_devscript_exec_report
 * @Description 脚本运行过程推送API
 * @FileName URoUkitSmartIntelligentDevscriptExecReportFormatter.java
 * 
 **/
public class URoUkitSmartIntelligentDevscriptExecReportFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevscriptExecReport.IntelligentDevscriptExecReportResponse> {

    private URoUkitSmartIntelligentDevscriptExecReportFormatter(){}

    public static final URoUkitSmartIntelligentDevscriptExecReportFormatter INSTANCE = new URoUkitSmartIntelligentDevscriptExecReportFormatter();

    @Override
    public URoResponse<PbIntelligentDevscriptExecReport.IntelligentDevscriptExecReportResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevscriptExecReport.IntelligentDevscriptExecReportResponse data;
        try {
            PbIntelligentDevscriptExecReport.IntelligentDevscriptExecReportResponse response = PbIntelligentDevscriptExecReport.IntelligentDevscriptExecReportResponse.parseFrom(bizData);
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
