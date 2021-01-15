package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevUpdateReport;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

import java.util.List;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/22
 * @CmdId 2506
 * @FunctionName intelligent_dev_update_report
 * @Description 外设变更通知
 * @FileName URoUkitSmartIntelligentDevUpdateReportFormatter.java
 * 
 **/
public class URoUkitSmartIntelligentDevUpdateReportFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevUpdateReport.IntelligentDevUpdateReportResponse> {

    private URoUkitSmartIntelligentDevUpdateReportFormatter(){}

    public static final URoUkitSmartIntelligentDevUpdateReportFormatter INSTANCE = new URoUkitSmartIntelligentDevUpdateReportFormatter();

    @Override
    public URoResponse<PbIntelligentDevUpdateReport.IntelligentDevUpdateReportResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevUpdateReport.IntelligentDevUpdateReportResponse data;
        try {
            PbIntelligentDevUpdateReport.IntelligentDevUpdateReportResponse response = PbIntelligentDevUpdateReport.IntelligentDevUpdateReportResponse.parseFrom(bizData);
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
