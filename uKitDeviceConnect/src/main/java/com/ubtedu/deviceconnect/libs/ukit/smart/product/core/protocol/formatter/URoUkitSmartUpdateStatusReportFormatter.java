package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbUpdateStatusReport;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/22
 * @CmdId 258
 * @FunctionName update_status_report
 * @Description 设备升级状态上报
 * @FileName URoUkitSmartUpdateStatusReportFormatter.java
 * 
 **/
public class URoUkitSmartUpdateStatusReportFormatter extends URoUkitSmartCommandFormatter<PbUpdateStatusReport.UpdateStatusReportResponse> {

    private URoUkitSmartUpdateStatusReportFormatter(){}

    public static final URoUkitSmartUpdateStatusReportFormatter INSTANCE = new URoUkitSmartUpdateStatusReportFormatter();

    @Override
    public URoResponse<PbUpdateStatusReport.UpdateStatusReportResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbUpdateStatusReport.UpdateStatusReportResponse data;
        try {
            PbUpdateStatusReport.UpdateStatusReportResponse response = PbUpdateStatusReport.UpdateStatusReportResponse.parseFrom(bizData);
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
