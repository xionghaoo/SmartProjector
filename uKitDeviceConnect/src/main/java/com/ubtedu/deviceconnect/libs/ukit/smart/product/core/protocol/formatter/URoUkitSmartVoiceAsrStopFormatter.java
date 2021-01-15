package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVoiceAsrStop;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/12/04
 * @CmdId 1033
 * @FunctionName voice_asr_stop
 * @Description 停止ASR
 * @FileName URoUkitSmartVoiceAsrStopFormatter.java
 * 
 **/
 public class URoUkitSmartVoiceAsrStopFormatter extends URoUkitSmartCommandFormatter<PbVoiceAsrStop.VoiceAsrStopResponse> {

    private URoUkitSmartVoiceAsrStopFormatter(){}

    public static final URoUkitSmartVoiceAsrStopFormatter INSTANCE = new URoUkitSmartVoiceAsrStopFormatter();

    @Override
    public URoResponse<PbVoiceAsrStop.VoiceAsrStopResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVoiceAsrStop.VoiceAsrStopResponse data;
        try {
            PbVoiceAsrStop.VoiceAsrStopResponse response = PbVoiceAsrStop.VoiceAsrStopResponse.parseFrom(bizData);
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
