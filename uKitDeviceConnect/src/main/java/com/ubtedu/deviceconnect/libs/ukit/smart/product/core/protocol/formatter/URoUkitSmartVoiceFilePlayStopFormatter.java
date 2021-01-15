package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVoiceFilePlayStop;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/12/04
 * @CmdId 1028
 * @FunctionName voice_file_play_stop
 * @Description 停止播放语音文件
 * @FileName URoUkitSmartVoiceFilePlayStopFormatter.java
 * 
 **/
 public class URoUkitSmartVoiceFilePlayStopFormatter extends URoUkitSmartCommandFormatter<PbVoiceFilePlayStop.VoiceFilePlayStopResponse> {

    private URoUkitSmartVoiceFilePlayStopFormatter(){}

    public static final URoUkitSmartVoiceFilePlayStopFormatter INSTANCE = new URoUkitSmartVoiceFilePlayStopFormatter();

    @Override
    public URoResponse<PbVoiceFilePlayStop.VoiceFilePlayStopResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVoiceFilePlayStop.VoiceFilePlayStopResponse data;
        try {
            PbVoiceFilePlayStop.VoiceFilePlayStopResponse response = PbVoiceFilePlayStop.VoiceFilePlayStopResponse.parseFrom(bizData);
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
