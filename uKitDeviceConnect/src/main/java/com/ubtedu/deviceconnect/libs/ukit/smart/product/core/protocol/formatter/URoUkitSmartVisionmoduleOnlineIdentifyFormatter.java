package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVisionmoduleOnlineIdentify;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/07/01
 * @CmdId 1011
 * @FunctionName visionmodule_online_identify
 * @Description 在线识别
 * @FileName URoUkitSmartVisionmoduleOnlineIdentifyFormatter.java
 * 
 **/
 public class URoUkitSmartVisionmoduleOnlineIdentifyFormatter extends URoUkitSmartCommandFormatter<PbVisionmoduleOnlineIdentify.VisionmoduleOnlineIdentifyResponse> {

    private URoUkitSmartVisionmoduleOnlineIdentifyFormatter(){}

    public static final URoUkitSmartVisionmoduleOnlineIdentifyFormatter INSTANCE = new URoUkitSmartVisionmoduleOnlineIdentifyFormatter();

    @Override
    public URoResponse<PbVisionmoduleOnlineIdentify.VisionmoduleOnlineIdentifyResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVisionmoduleOnlineIdentify.VisionmoduleOnlineIdentifyResponse data;
        try {
            PbVisionmoduleOnlineIdentify.VisionmoduleOnlineIdentifyResponse response = PbVisionmoduleOnlineIdentify.VisionmoduleOnlineIdentifyResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbVisionmoduleOnlineIdentify.VisionmoduleOnlineIdentifyRequest.Builder builder = PbVisionmoduleOnlineIdentify.VisionmoduleOnlineIdentifyRequest.newBuilder();
        builder.setUrl(request.getParameter("url", ""));
        builder.setRequestBody(request.getParameter("request_body", ""));
        builder.addImgPh(request.getParameter("img_ph", ""));
        return builder.build().toByteArray();
    }

}
