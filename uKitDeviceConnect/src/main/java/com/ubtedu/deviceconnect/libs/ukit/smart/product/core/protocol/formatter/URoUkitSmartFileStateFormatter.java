package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbFileState;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 112
 * @FunctionName file_state
 * @Description 获取文件信息
 * @FileName URoUkitSmartFileStateFormatter.java
 * 
 **/
public class URoUkitSmartFileStateFormatter extends URoUkitSmartCommandFormatter<PbFileState.FileStateResponse> {

    private URoUkitSmartFileStateFormatter(){}

    public static final URoUkitSmartFileStateFormatter INSTANCE = new URoUkitSmartFileStateFormatter();

    @Override
    public URoResponse<PbFileState.FileStateResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbFileState.FileStateResponse data;
        URoLogUtils.e("PbFileState: %b", success);
        try {
            PbFileState.FileStateResponse response = PbFileState.FileStateResponse.parseFrom(bizData);
            URoLogUtils.d("FileName: %s", response.getFileName());
            URoLogUtils.d("FileSize: %d", response.getFileSize());
            URoLogUtils.d("FileMode: %d", response.getFileMode());
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbFileState.FileStateRequest.Builder builder = PbFileState.FileStateRequest.newBuilder();
        builder.setPath(request.getParameter("path", ""));
        return builder.build().toByteArray();
    }

}
