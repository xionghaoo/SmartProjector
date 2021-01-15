package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbNetworkInfoReport;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/07/01
 * @CmdId 306
 * @FunctionName network_info_report
 * @Description 网络连接信息上报
 * @FileName URoUkitSmartNetworkInfoReportFormatter.java
 * 
 **/
 public class URoUkitSmartNetworkInfoReportFormatter extends URoUkitSmartCommandFormatter<PbNetworkInfoReport.NetworkInfoReportResponse> {

    private URoUkitSmartNetworkInfoReportFormatter(){}

    public static final URoUkitSmartNetworkInfoReportFormatter INSTANCE = new URoUkitSmartNetworkInfoReportFormatter();

    @Override
    public URoResponse<PbNetworkInfoReport.NetworkInfoReportResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbNetworkInfoReport.NetworkInfoReportResponse data;
        try {
            PbNetworkInfoReport.NetworkInfoReportResponse response = PbNetworkInfoReport.NetworkInfoReportResponse.parseFrom(bizData);
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
