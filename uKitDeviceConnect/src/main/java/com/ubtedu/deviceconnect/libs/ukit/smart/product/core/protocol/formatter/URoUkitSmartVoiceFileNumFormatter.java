package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVoiceFileNum;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/12/13
 * @CmdId 1039
 * @FunctionName voice_file_num
 * @Description 获取录音文件数量
 * @FileName URoUkitSmartVoiceFileNumFormatter.java
 * 
 **/
 public class URoUkitSmartVoiceFileNumFormatter extends URoUkitSmartCommandFormatter<PbVoiceFileNum.VoiceFileNumResponse> {

    private URoUkitSmartVoiceFileNumFormatter(){}

    public static final URoUkitSmartVoiceFileNumFormatter INSTANCE = new URoUkitSmartVoiceFileNumFormatter();

    @Override
    public URoResponse<PbVoiceFileNum.VoiceFileNumResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVoiceFileNum.VoiceFileNumResponse data;
        try {
            PbVoiceFileNum.VoiceFileNumResponse response = PbVoiceFileNum.VoiceFileNumResponse.parseFrom(bizData);
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
