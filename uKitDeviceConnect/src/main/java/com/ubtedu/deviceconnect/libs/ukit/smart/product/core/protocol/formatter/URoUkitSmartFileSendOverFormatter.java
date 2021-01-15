package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbFileSendOver;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 103
 * @FunctionName file_send_over
 * @Description 下发文件完成
 * @FileName URoUkitSmartFileSendOverFormatter.java
 * 
 **/
public class URoUkitSmartFileSendOverFormatter extends URoUkitSmartCommandFormatter<PbFileSendOver.FileSendOverResponse> {

    private URoUkitSmartFileSendOverFormatter(){}

    public static final URoUkitSmartFileSendOverFormatter INSTANCE = new URoUkitSmartFileSendOverFormatter();

    @Override
    public URoResponse<PbFileSendOver.FileSendOverResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbFileSendOver.FileSendOverResponse data;
        try {
            PbFileSendOver.FileSendOverResponse response = PbFileSendOver.FileSendOverResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbFileSendOver.FileSendOverRequest.Builder builder = PbFileSendOver.FileSendOverRequest.newBuilder();
        builder.setFileSize(request.getParameter("file_size", 0));
        builder.setCrc32(request.getParameter("crc32", 0));
        return builder.build().toByteArray();
    }

}
