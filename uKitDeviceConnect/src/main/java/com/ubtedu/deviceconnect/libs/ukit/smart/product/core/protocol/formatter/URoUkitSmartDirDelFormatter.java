package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbDirDel;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 114
 * @FunctionName dir_del
 * @Description 删除文件夹
 * @FileName URoUkitSmartDirDelFormatter.java
 * 
 **/
public class URoUkitSmartDirDelFormatter extends URoUkitSmartCommandFormatter<PbDirDel.DirDelResponse> {

    private URoUkitSmartDirDelFormatter(){}

    public static final URoUkitSmartDirDelFormatter INSTANCE = new URoUkitSmartDirDelFormatter();

    @Override
    public URoResponse<PbDirDel.DirDelResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbDirDel.DirDelResponse data;
        URoLogUtils.e("PbDirDel: %b", success);
        try {
            PbDirDel.DirDelResponse response = PbDirDel.DirDelResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbDirDel.DirDelRequest.Builder builder = PbDirDel.DirDelRequest.newBuilder();
        builder.setPath(request.getParameter("path", ""));
        return builder.build().toByteArray();
    }

}
