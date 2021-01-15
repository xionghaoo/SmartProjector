package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbFileSendStart;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 101
 * @FunctionName file_send_start
 * @Description 启动下发文件
 * @FileName URoUkitSmartFileSendStartFormatter.java
 * 
 **/
public class URoUkitSmartFileSendStartFormatter extends URoUkitSmartCommandFormatter<PbFileSendStart.FileSendStartResponse> {

    private URoUkitSmartFileSendStartFormatter(){}

    public static final URoUkitSmartFileSendStartFormatter INSTANCE = new URoUkitSmartFileSendStartFormatter();

    @Override
    public URoResponse<PbFileSendStart.FileSendStartResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbFileSendStart.FileSendStartResponse data;
        try {
            PbFileSendStart.FileSendStartResponse response = PbFileSendStart.FileSendStartResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbFileSendStart.FileSendStartRequest.Builder builder = PbFileSendStart.FileSendStartRequest.newBuilder();
        builder.setPath(request.getParameter("path", ""));
        builder.setPackNum(request.getParameter("pack_num", 0));
        builder.setFileSize(request.getParameter("file_size", 0));
        return builder.build().toByteArray();
    }

}
