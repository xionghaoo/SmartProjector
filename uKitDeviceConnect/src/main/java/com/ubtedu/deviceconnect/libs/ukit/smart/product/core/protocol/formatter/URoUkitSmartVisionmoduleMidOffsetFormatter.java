package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVisionmoduleMidOffset;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/07/01
 * @CmdId 1003
 * @FunctionName visionmodule_mid_offset
 * @Description 中部偏移量
 * @FileName URoUkitSmartVisionmoduleMidOffsetFormatter.java
 * 
 **/
 public class URoUkitSmartVisionmoduleMidOffsetFormatter extends URoUkitSmartCommandFormatter<PbVisionmoduleMidOffset.VisionmoduleMidOffsetResponse> {

    private URoUkitSmartVisionmoduleMidOffsetFormatter(){}

    public static final URoUkitSmartVisionmoduleMidOffsetFormatter INSTANCE = new URoUkitSmartVisionmoduleMidOffsetFormatter();

    @Override
    public URoResponse<PbVisionmoduleMidOffset.VisionmoduleMidOffsetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVisionmoduleMidOffset.VisionmoduleMidOffsetResponse data;
        try {
            PbVisionmoduleMidOffset.VisionmoduleMidOffsetResponse response = PbVisionmoduleMidOffset.VisionmoduleMidOffsetResponse.parseFrom(bizData);
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
