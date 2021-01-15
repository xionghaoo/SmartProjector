package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbDirFCntGet;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 115
 * @FunctionName dir_f_cnt_get
 * @Description 获取文件夹中的文件或目录数量
 * @FileName URoUkitSmartDirFCntGetFormatter.java
 * 
 **/
public class URoUkitSmartDirFCntGetFormatter extends URoUkitSmartCommandFormatter<PbDirFCntGet.DirFCntGetResponse> {

    private URoUkitSmartDirFCntGetFormatter(){}

    public static final URoUkitSmartDirFCntGetFormatter INSTANCE = new URoUkitSmartDirFCntGetFormatter();

    @Override
    public URoResponse<PbDirFCntGet.DirFCntGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbDirFCntGet.DirFCntGetResponse data;
        try {
            PbDirFCntGet.DirFCntGetResponse response = PbDirFCntGet.DirFCntGetResponse.parseFrom(bizData);
            URoLogUtils.e("PbDirFCntGet: %d", response.getFileNum());
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbDirFCntGet.DirFCntGetRequest.Builder builder = PbDirFCntGet.DirFCntGetRequest.newBuilder();
        builder.setPath(request.getParameter("path", ""));
        return builder.build().toByteArray();
    }

}
