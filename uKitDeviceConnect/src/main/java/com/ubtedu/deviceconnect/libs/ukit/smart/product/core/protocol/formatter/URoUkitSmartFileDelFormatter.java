package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbFileDel;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 110
 * @FunctionName file_del
 * @Description 删除文件
 * @FileName URoUkitSmartFileDelFormatter.java
 * 
 **/
public class URoUkitSmartFileDelFormatter extends URoUkitSmartCommandFormatter<PbFileDel.FileDelResponse> {

    private URoUkitSmartFileDelFormatter(){}

    public static final URoUkitSmartFileDelFormatter INSTANCE = new URoUkitSmartFileDelFormatter();

    @Override
    public URoResponse<PbFileDel.FileDelResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbFileDel.FileDelResponse data;
        URoLogUtils.e("PbFileDel: %b", success);
        try {
            PbFileDel.FileDelResponse response = PbFileDel.FileDelResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbFileDel.FileDelRequest.Builder builder = PbFileDel.FileDelRequest.newBuilder();
        builder.setPath(request.getParameter("path", ""));
        return builder.build().toByteArray();
    }

}
