package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVoiceFileRename;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/12/04
 * @CmdId 1021
 * @FunctionName voice_file_rename
 * @Description 语音文件重命名
 * @FileName URoUkitSmartVoiceFileRenameFormatter.java
 * 
 **/
 public class URoUkitSmartVoiceFileRenameFormatter extends URoUkitSmartCommandFormatter<PbVoiceFileRename.VoiceFileRenameResponse> {

    private URoUkitSmartVoiceFileRenameFormatter(){}

    public static final URoUkitSmartVoiceFileRenameFormatter INSTANCE = new URoUkitSmartVoiceFileRenameFormatter();

    @Override
    public URoResponse<PbVoiceFileRename.VoiceFileRenameResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVoiceFileRename.VoiceFileRenameResponse data;
        try {
            PbVoiceFileRename.VoiceFileRenameResponse response = PbVoiceFileRename.VoiceFileRenameResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbVoiceFileRename.VoiceFileRenameRequest.Builder builder = PbVoiceFileRename.VoiceFileRenameRequest.newBuilder();
        builder.setOldName(request.getParameter("old_name", ""));
        builder.setNewName(request.getParameter("new_name", ""));
        return builder.build().toByteArray();
    }

}
