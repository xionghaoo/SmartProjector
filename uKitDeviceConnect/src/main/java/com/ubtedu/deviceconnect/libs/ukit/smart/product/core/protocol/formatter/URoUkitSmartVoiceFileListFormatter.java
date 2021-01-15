package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVoiceFileList;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/12/04
 * @CmdId 1022
 * @FunctionName voice_file_list
 * @Description 获取录音文件信息列表
 * @FileName URoUkitSmartVoiceFileListFormatter.java
 * 
 **/
 public class URoUkitSmartVoiceFileListFormatter extends URoUkitSmartCommandFormatter<PbVoiceFileList.VoiceFileListResponse> {

    private URoUkitSmartVoiceFileListFormatter(){}

    public static final URoUkitSmartVoiceFileListFormatter INSTANCE = new URoUkitSmartVoiceFileListFormatter();

    @Override
    public URoResponse<PbVoiceFileList.VoiceFileListResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVoiceFileList.VoiceFileListResponse data;
        try {
            PbVoiceFileList.VoiceFileListResponse response = PbVoiceFileList.VoiceFileListResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        return EMPTY_REQUEST_DATA;
    }

}
