package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbPcLinkInfoReport;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/07/01
 * @CmdId 401
 * @FunctionName pc_link_info_report
 * @Description PC连接信息上报
 * @FileName URoUkitSmartPcLinkInfoReportFormatter.java
 * 
 **/
 public class URoUkitSmartPcLinkInfoReportFormatter extends URoUkitSmartCommandFormatter<PbPcLinkInfoReport.PcLinkInfoReportResponse> {

    private URoUkitSmartPcLinkInfoReportFormatter(){}

    public static final URoUkitSmartPcLinkInfoReportFormatter INSTANCE = new URoUkitSmartPcLinkInfoReportFormatter();

    @Override
    public URoResponse<PbPcLinkInfoReport.PcLinkInfoReportResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbPcLinkInfoReport.PcLinkInfoReportResponse data;
        try {
            PbPcLinkInfoReport.PcLinkInfoReportResponse response = PbPcLinkInfoReport.PcLinkInfoReportResponse.parseFrom(bizData);
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
