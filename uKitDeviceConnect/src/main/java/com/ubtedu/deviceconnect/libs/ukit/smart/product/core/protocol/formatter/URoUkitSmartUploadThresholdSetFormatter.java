package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbUploadThresholdSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/10/24
 * @CmdId 15
 * @FunctionName upload_threshold_set
 * @Description 设置阈值上报的阈值
 * @FileName URoUkitSmartUploadThresholdSetFormatter.java
 * 
 **/
 public class URoUkitSmartUploadThresholdSetFormatter extends URoUkitSmartCommandFormatter<PbUploadThresholdSet.UploadThresholdSetResponse> {

    private URoUkitSmartUploadThresholdSetFormatter(){}

    public static final URoUkitSmartUploadThresholdSetFormatter INSTANCE = new URoUkitSmartUploadThresholdSetFormatter();

    @Override
    public URoResponse<PbUploadThresholdSet.UploadThresholdSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbUploadThresholdSet.UploadThresholdSetResponse data;
        try {
            PbUploadThresholdSet.UploadThresholdSetResponse response = PbUploadThresholdSet.UploadThresholdSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        int[] thresholds = request.getParameter("thresholds", new int[0]);
        PbUploadThresholdSet.UploadThresholdSetRequest.Builder builder = PbUploadThresholdSet.UploadThresholdSetRequest.newBuilder();
        for(int threshold : thresholds) {
            builder.addThreshold(threshold);
        }
        return builder.build().toByteArray();
    }

}
