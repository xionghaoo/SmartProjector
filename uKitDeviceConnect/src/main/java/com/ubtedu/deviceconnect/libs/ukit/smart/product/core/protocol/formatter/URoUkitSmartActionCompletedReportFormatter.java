package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbActionCompletedReport;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/22
 * @CmdId 255
 * @FunctionName action_completed_report
 * @Description 主动上报动作完成
 * @FileName URoUkitSmartActionCompletedReportFormatter.java
 * 
 **/
public class URoUkitSmartActionCompletedReportFormatter extends URoUkitSmartCommandFormatter<PbActionCompletedReport.ActionCompletedReportResponse> {

    private URoUkitSmartActionCompletedReportFormatter(){}

    public static final URoUkitSmartActionCompletedReportFormatter INSTANCE = new URoUkitSmartActionCompletedReportFormatter();

    @Override
    public URoResponse<PbActionCompletedReport.ActionCompletedReportResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbActionCompletedReport.ActionCompletedReportResponse data;
        try {
            PbActionCompletedReport.ActionCompletedReportResponse response = PbActionCompletedReport.ActionCompletedReportResponse.parseFrom(bizData);
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
