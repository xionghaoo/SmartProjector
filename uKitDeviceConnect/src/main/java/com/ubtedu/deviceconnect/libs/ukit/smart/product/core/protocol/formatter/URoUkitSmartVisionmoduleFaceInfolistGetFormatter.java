package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVisionmoduleFaceInfolistGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/07/01
 * @CmdId 1007
 * @FunctionName visionmodule_face_infolist_get
 * @Description 人脸信息列表获取
 * @FileName URoUkitSmartVisionmoduleFaceInfolistGetFormatter.java
 * 
 **/
 public class URoUkitSmartVisionmoduleFaceInfolistGetFormatter extends URoUkitSmartCommandFormatter<PbVisionmoduleFaceInfolistGet.VisionmoduleFaceInfolistGetResponse> {

    private URoUkitSmartVisionmoduleFaceInfolistGetFormatter(){}

    public static final URoUkitSmartVisionmoduleFaceInfolistGetFormatter INSTANCE = new URoUkitSmartVisionmoduleFaceInfolistGetFormatter();

    @Override
    public URoResponse<PbVisionmoduleFaceInfolistGet.VisionmoduleFaceInfolistGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVisionmoduleFaceInfolistGet.VisionmoduleFaceInfolistGetResponse data;
        try {
            PbVisionmoduleFaceInfolistGet.VisionmoduleFaceInfolistGetResponse response = PbVisionmoduleFaceInfolistGet.VisionmoduleFaceInfolistGetResponse.parseFrom(bizData);
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
