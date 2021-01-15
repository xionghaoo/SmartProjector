package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevWifiInfoReport;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/09/27
 * @CmdId 2513
 * @FunctionName intelligent_dev_wifi_info_report
 * @Description WIIF网络信息上报
 * @FileName URoUkitSmartIntelligentDevWifiInfoReportFormatter.java
 * 
 **/
 public class URoUkitSmartIntelligentDevWifiInfoReportFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevWifiInfoReport.IntelligentDevWifiInfoReportResponse> {

    private URoUkitSmartIntelligentDevWifiInfoReportFormatter(){}

    public static final URoUkitSmartIntelligentDevWifiInfoReportFormatter INSTANCE = new URoUkitSmartIntelligentDevWifiInfoReportFormatter();

    @Override
    public URoResponse<PbIntelligentDevWifiInfoReport.IntelligentDevWifiInfoReportResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevWifiInfoReport.IntelligentDevWifiInfoReportResponse data;
        try {
            PbIntelligentDevWifiInfoReport.IntelligentDevWifiInfoReportResponse response = PbIntelligentDevWifiInfoReport.IntelligentDevWifiInfoReportResponse.parseFrom(bizData);
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
