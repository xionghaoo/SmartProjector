package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbBtInfoReport;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/07/01
 * @CmdId 351
 * @FunctionName bt_info_report
 * @Description 蓝牙连接信息上报
 * @FileName URoUkitSmartBtInfoReportFormatter.java
 * 
 **/
 public class URoUkitSmartBtInfoReportFormatter extends URoUkitSmartCommandFormatter<PbBtInfoReport.BtInfoReportResponse> {

    private URoUkitSmartBtInfoReportFormatter(){}

    public static final URoUkitSmartBtInfoReportFormatter INSTANCE = new URoUkitSmartBtInfoReportFormatter();

    @Override
    public URoResponse<PbBtInfoReport.BtInfoReportResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbBtInfoReport.BtInfoReportResponse data;
        try {
            PbBtInfoReport.BtInfoReportResponse response = PbBtInfoReport.BtInfoReportResponse.parseFrom(bizData);
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
