package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbDirFInfoGet;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 116
 * @FunctionName dir_f_info_get
 * @Description 获取文件夹中对应的文件/文件夹信息
 * @FileName URoUkitSmartDirFInfoGetFormatter.java
 * 
 **/
public class URoUkitSmartDirFInfoGetFormatter extends URoUkitSmartCommandFormatter<PbDirFInfoGet.DirFInfoGetResponse> {

    private URoUkitSmartDirFInfoGetFormatter(){}

    public static final URoUkitSmartDirFInfoGetFormatter INSTANCE = new URoUkitSmartDirFInfoGetFormatter();

    @Override
    public URoResponse<PbDirFInfoGet.DirFInfoGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbDirFInfoGet.DirFInfoGetResponse data;
        try {
            PbDirFInfoGet.DirFInfoGetResponse response = PbDirFInfoGet.DirFInfoGetResponse.parseFrom(bizData);
            URoLogUtils.e("PbDirFInfoGet: %s %d %d", response.getFileName(), response.getFileSize(), response.getFileMode());
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbDirFInfoGet.DirFInfoGetRequest.Builder builder = PbDirFInfoGet.DirFInfoGetRequest.newBuilder();
        builder.setPath(request.getParameter("path", ""));
        builder.setFileIdx(request.getParameter("file_idx", 0));
        return builder.build().toByteArray();
    }

}
