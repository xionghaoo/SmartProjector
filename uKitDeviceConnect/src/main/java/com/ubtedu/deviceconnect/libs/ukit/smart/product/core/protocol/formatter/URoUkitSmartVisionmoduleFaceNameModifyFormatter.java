package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVisionmoduleFaceNameModify;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/07/01
 * @CmdId 1009
 * @FunctionName visionmodule_face_name_modify
 * @Description 人脸名称修改
 * @FileName URoUkitSmartVisionmoduleFaceNameModifyFormatter.java
 * 
 **/
 public class URoUkitSmartVisionmoduleFaceNameModifyFormatter extends URoUkitSmartCommandFormatter<PbVisionmoduleFaceNameModify.VisionmoduleFaceNameModifyResponse> {

    private URoUkitSmartVisionmoduleFaceNameModifyFormatter(){}

    public static final URoUkitSmartVisionmoduleFaceNameModifyFormatter INSTANCE = new URoUkitSmartVisionmoduleFaceNameModifyFormatter();

    @Override
    public URoResponse<PbVisionmoduleFaceNameModify.VisionmoduleFaceNameModifyResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVisionmoduleFaceNameModify.VisionmoduleFaceNameModifyResponse data;
        try {
            PbVisionmoduleFaceNameModify.VisionmoduleFaceNameModifyResponse response = PbVisionmoduleFaceNameModify.VisionmoduleFaceNameModifyResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbVisionmoduleFaceNameModify.VisionmoduleFaceNameModifyRequest.Builder builder = PbVisionmoduleFaceNameModify.VisionmoduleFaceNameModifyRequest.newBuilder();
        builder.setId(request.getParameter("id", -1));
        builder.setName(request.getParameter("name", ""));
        return builder.build().toByteArray();
    }

}
