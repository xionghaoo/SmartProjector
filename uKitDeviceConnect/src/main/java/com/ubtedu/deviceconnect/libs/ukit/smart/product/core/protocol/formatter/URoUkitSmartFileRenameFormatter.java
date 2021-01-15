package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbFileRename;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 111
 * @FunctionName file_rename
 * @Description 文件重命名
 * @FileName URoUkitSmartFileRenameFormatter.java
 * 
 **/
public class URoUkitSmartFileRenameFormatter extends URoUkitSmartCommandFormatter<PbFileRename.FileRenameResponse> {

    private URoUkitSmartFileRenameFormatter(){}

    public static final URoUkitSmartFileRenameFormatter INSTANCE = new URoUkitSmartFileRenameFormatter();

    @Override
    public URoResponse<PbFileRename.FileRenameResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbFileRename.FileRenameResponse data;
        URoLogUtils.e("PbFileRename: %b", success);
        try {
            PbFileRename.FileRenameResponse response = PbFileRename.FileRenameResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbFileRename.FileRenameRequest.Builder builder = PbFileRename.FileRenameRequest.newBuilder();
        builder.setNewName(request.getParameter("new_name", ""));
        builder.setOldName(request.getParameter("old_name", ""));
        return builder.build().toByteArray();
    }

}
