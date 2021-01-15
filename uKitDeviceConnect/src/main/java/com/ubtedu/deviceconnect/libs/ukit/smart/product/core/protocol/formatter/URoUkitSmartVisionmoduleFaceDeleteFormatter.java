package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVisionmoduleFaceDelete;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/07/01
 * @CmdId 1010
 * @FunctionName visionmodule_face_delete
 * @Description 人脸信息删除
 * @FileName URoUkitSmartVisionmoduleFaceDeleteFormatter.java
 * 
 **/
 public class URoUkitSmartVisionmoduleFaceDeleteFormatter extends URoUkitSmartCommandFormatter<PbVisionmoduleFaceDelete.VisionmoduleFaceDeleteResponse> {

    private URoUkitSmartVisionmoduleFaceDeleteFormatter(){}

    public static final URoUkitSmartVisionmoduleFaceDeleteFormatter INSTANCE = new URoUkitSmartVisionmoduleFaceDeleteFormatter();

    @Override
    public URoResponse<PbVisionmoduleFaceDelete.VisionmoduleFaceDeleteResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVisionmoduleFaceDelete.VisionmoduleFaceDeleteResponse data;
        try {
            PbVisionmoduleFaceDelete.VisionmoduleFaceDeleteResponse response = PbVisionmoduleFaceDelete.VisionmoduleFaceDeleteResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbVisionmoduleFaceDelete.VisionmoduleFaceDeleteRequest.Builder builder = PbVisionmoduleFaceDelete.VisionmoduleFaceDeleteRequest.newBuilder();
        builder.setId(request.getParameter("id", -1));
        return builder.build().toByteArray();
    }

}
