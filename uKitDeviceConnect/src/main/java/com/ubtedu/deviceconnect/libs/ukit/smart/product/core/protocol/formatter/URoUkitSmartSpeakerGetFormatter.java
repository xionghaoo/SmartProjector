package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbSpeakerGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/26
 * @CmdId 1000
 * @FunctionName speaker_get
 * @Description 读喇叭信息
 * @FileName URoUkitSmartSpeakerGetFormatter.java
 * 
 **/
public class URoUkitSmartSpeakerGetFormatter extends URoUkitSmartCommandFormatter<PbSpeakerGet.SpeakerGetResponse> {

    private URoUkitSmartSpeakerGetFormatter(){}

    public static final URoUkitSmartSpeakerGetFormatter INSTANCE = new URoUkitSmartSpeakerGetFormatter();

    @Override
    public URoResponse<PbSpeakerGet.SpeakerGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbSpeakerGet.SpeakerGetResponse data;
        try {
            PbSpeakerGet.SpeakerGetResponse response = PbSpeakerGet.SpeakerGetResponse.parseFrom(bizData);
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
