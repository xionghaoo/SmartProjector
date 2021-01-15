package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevNetworkInfoReport;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/10/14
 * @CmdId 2519
 * @FunctionName intelligent_dev_network_info_report
 * @Description 网络连接信息上报
 * @FileName URoUkitSmartIntelligentDevNetworkInfoReportFormatter.java
 * 
 **/
 public class URoUkitSmartIntelligentDevNetworkInfoReportFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevNetworkInfoReport.IntelligentDevNetworkInfoReportResponse> {

    private URoUkitSmartIntelligentDevNetworkInfoReportFormatter(){}

    public static final URoUkitSmartIntelligentDevNetworkInfoReportFormatter INSTANCE = new URoUkitSmartIntelligentDevNetworkInfoReportFormatter();

    @Override
    public URoResponse<PbIntelligentDevNetworkInfoReport.IntelligentDevNetworkInfoReportResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevNetworkInfoReport.IntelligentDevNetworkInfoReportResponse data;
        try {
            PbIntelligentDevNetworkInfoReport.IntelligentDevNetworkInfoReportResponse response = PbIntelligentDevNetworkInfoReport.IntelligentDevNetworkInfoReportResponse.parseFrom(bizData);
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
