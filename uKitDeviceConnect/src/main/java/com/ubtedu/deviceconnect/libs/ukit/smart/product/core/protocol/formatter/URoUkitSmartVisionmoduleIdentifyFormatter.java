package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVisionmoduleIdentify;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/07/01
 * @CmdId 1005
 * @FunctionName visionmodule_identify
 * @Description 识别指定人/物
 * @FileName URoUkitSmartVisionmoduleIdentifyFormatter.java
 * 
 **/
 public class URoUkitSmartVisionmoduleIdentifyFormatter extends URoUkitSmartCommandFormatter<PbVisionmoduleIdentify.VisionmoduleIdentifyResponse> {

    private URoUkitSmartVisionmoduleIdentifyFormatter(){}

    public static final URoUkitSmartVisionmoduleIdentifyFormatter INSTANCE = new URoUkitSmartVisionmoduleIdentifyFormatter();

    @Override
    public URoResponse<PbVisionmoduleIdentify.VisionmoduleIdentifyResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVisionmoduleIdentify.VisionmoduleIdentifyResponse data;
        try {
            PbVisionmoduleIdentify.VisionmoduleIdentifyResponse response = PbVisionmoduleIdentify.VisionmoduleIdentifyResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbVisionmoduleIdentify.VisionmoduleIdentifyRequest.Builder builder = PbVisionmoduleIdentify.VisionmoduleIdentifyRequest.newBuilder();
        builder.setModel(request.getParameter("model", 0));
        return builder.build().toByteArray();
    }

}
