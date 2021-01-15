package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVisionmoduleJpegQualitySet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/07/01
 * @CmdId 1004
 * @FunctionName visionmodule_jpeg_quality_set
 * @Description jpeg图传质量设置
 * @FileName URoUkitSmartVisionmoduleJpegQualitySetFormatter.java
 * 
 **/
 public class URoUkitSmartVisionmoduleJpegQualitySetFormatter extends URoUkitSmartCommandFormatter<PbVisionmoduleJpegQualitySet.VisionmoduleJpegQualitySetResponse> {

    private URoUkitSmartVisionmoduleJpegQualitySetFormatter(){}

    public static final URoUkitSmartVisionmoduleJpegQualitySetFormatter INSTANCE = new URoUkitSmartVisionmoduleJpegQualitySetFormatter();

    @Override
    public URoResponse<PbVisionmoduleJpegQualitySet.VisionmoduleJpegQualitySetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVisionmoduleJpegQualitySet.VisionmoduleJpegQualitySetResponse data;
        try {
            PbVisionmoduleJpegQualitySet.VisionmoduleJpegQualitySetResponse response = PbVisionmoduleJpegQualitySet.VisionmoduleJpegQualitySetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbVisionmoduleJpegQualitySet.VisionmoduleJpegQualitySetRequest.Builder builder = PbVisionmoduleJpegQualitySet.VisionmoduleJpegQualitySetRequest.newBuilder();
        builder.setQuality(request.getParameter("quality", 0));
        return builder.build().toByteArray();
    }

}
