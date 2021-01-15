package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbDirMake;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 113
 * @FunctionName dir_make
 * @Description 新建文件夹
 * @FileName URoUkitSmartDirMakeFormatter.java
 * 
 **/
public class URoUkitSmartDirMakeFormatter extends URoUkitSmartCommandFormatter<PbDirMake.DirMakeResponse> {

    private URoUkitSmartDirMakeFormatter(){}

    public static final URoUkitSmartDirMakeFormatter INSTANCE = new URoUkitSmartDirMakeFormatter();

    @Override
    public URoResponse<PbDirMake.DirMakeResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbDirMake.DirMakeResponse data;
        URoLogUtils.e("PbDirMake: %b", success);
        try {
            PbDirMake.DirMakeResponse response = PbDirMake.DirMakeResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbDirMake.DirMakeRequest.Builder builder = PbDirMake.DirMakeRequest.newBuilder();
        builder.setPath(request.getParameter("path", ""));
        return builder.build().toByteArray();
    }

}
