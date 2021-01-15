package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVisionmoduleFaceRecord;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/07/01
 * @CmdId 1006
 * @FunctionName visionmodule_face_record
 * @Description 人脸录入
 * @FileName URoUkitSmartVisionmoduleFaceRecordFormatter.java
 * 
 **/
 public class URoUkitSmartVisionmoduleFaceRecordFormatter extends URoUkitSmartCommandFormatter<PbVisionmoduleFaceRecord.VisionmoduleFaceRecordResponse> {

    private URoUkitSmartVisionmoduleFaceRecordFormatter(){}

    public static final URoUkitSmartVisionmoduleFaceRecordFormatter INSTANCE = new URoUkitSmartVisionmoduleFaceRecordFormatter();

    @Override
    public URoResponse<PbVisionmoduleFaceRecord.VisionmoduleFaceRecordResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVisionmoduleFaceRecord.VisionmoduleFaceRecordResponse data;
        try {
            PbVisionmoduleFaceRecord.VisionmoduleFaceRecordResponse response = PbVisionmoduleFaceRecord.VisionmoduleFaceRecordResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbVisionmoduleFaceRecord.VisionmoduleFaceRecordRequest.Builder builder = PbVisionmoduleFaceRecord.VisionmoduleFaceRecordRequest.newBuilder();
        builder.setId(request.getParameter("id", -1));
        builder.setName(request.getParameter("name", ""));
        return builder.build().toByteArray();
    }

}
