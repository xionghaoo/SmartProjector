package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbFileRecvStart;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 106
 * @FunctionName file_recv_start
 * @Description 启动回传文件
 * @FileName URoUkitSmartFileRecvStartFormatter.java
 * 
 **/
public class URoUkitSmartFileRecvStartFormatter extends URoUkitSmartCommandFormatter<PbFileRecvStart.FileRecvStartResponse> {

    private URoUkitSmartFileRecvStartFormatter(){}

    public static final URoUkitSmartFileRecvStartFormatter INSTANCE = new URoUkitSmartFileRecvStartFormatter();

    @Override
    public URoResponse<PbFileRecvStart.FileRecvStartResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbFileRecvStart.FileRecvStartResponse data;
        try {
            PbFileRecvStart.FileRecvStartResponse response = PbFileRecvStart.FileRecvStartResponse.parseFrom(bizData);
            URoLogUtils.d("PackNum: %d", response.getPackNum());
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbFileRecvStart.FileRecvStartRequest.Builder builder = PbFileRecvStart.FileRecvStartRequest.newBuilder();
        builder.setPath(request.getParameter("path", ""));
        return builder.build().toByteArray();
    }

}
