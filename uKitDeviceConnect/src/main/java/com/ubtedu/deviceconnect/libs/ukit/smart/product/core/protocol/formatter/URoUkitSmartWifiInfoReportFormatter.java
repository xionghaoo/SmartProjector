package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbWifiInfoReport;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/07/01
 * @CmdId 304
 * @FunctionName wifi_info_report
 * @Description WIIF网络信息上报
 * @FileName URoUkitSmartWifiInfoReportFormatter.java
 * 
 **/
 public class URoUkitSmartWifiInfoReportFormatter extends URoUkitSmartCommandFormatter<PbWifiInfoReport.WifiInfoReportResponse> {

    private URoUkitSmartWifiInfoReportFormatter(){}

    public static final URoUkitSmartWifiInfoReportFormatter INSTANCE = new URoUkitSmartWifiInfoReportFormatter();

    @Override
    public URoResponse<PbWifiInfoReport.WifiInfoReportResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbWifiInfoReport.WifiInfoReportResponse data;
        try {
            PbWifiInfoReport.WifiInfoReportResponse response = PbWifiInfoReport.WifiInfoReportResponse.parseFrom(bizData);
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
