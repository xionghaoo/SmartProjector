package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbBatteryStatusReport;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/10/15
 * @CmdId 1007
 * @FunctionName battery_status_report
 * @Description 获取电池上报
 * @FileName URoUkitSmartBatteryStatusReportFormatter.java
 * 
 **/
 public class URoUkitSmartBatteryStatusReportFormatter extends URoUkitSmartCommandFormatter<PbBatteryStatusReport.BatteryStatusReportResponse> {

    private URoUkitSmartBatteryStatusReportFormatter(){}

    public static final URoUkitSmartBatteryStatusReportFormatter INSTANCE = new URoUkitSmartBatteryStatusReportFormatter();

    @Override
    public URoResponse<PbBatteryStatusReport.BatteryStatusReportResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbBatteryStatusReport.BatteryStatusReportResponse data;
        try {
            PbBatteryStatusReport.BatteryStatusReportResponse response = PbBatteryStatusReport.BatteryStatusReportResponse.parseFrom(bizData);
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
