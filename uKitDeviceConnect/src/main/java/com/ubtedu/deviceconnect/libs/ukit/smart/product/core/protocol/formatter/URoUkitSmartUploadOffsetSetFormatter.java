package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbUploadOffsetSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/10/24
 * @CmdId 16
 * @FunctionName upload_offset_set
 * @Description 设置差值上报的差值
 * @FileName URoUkitSmartUploadOffsetSetFormatter.java
 * 
 **/
 public class URoUkitSmartUploadOffsetSetFormatter extends URoUkitSmartCommandFormatter<PbUploadOffsetSet.UploadOffsetSetResponse> {

    private URoUkitSmartUploadOffsetSetFormatter(){}

    public static final URoUkitSmartUploadOffsetSetFormatter INSTANCE = new URoUkitSmartUploadOffsetSetFormatter();

    @Override
    public URoResponse<PbUploadOffsetSet.UploadOffsetSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbUploadOffsetSet.UploadOffsetSetResponse data;
        try {
            PbUploadOffsetSet.UploadOffsetSetResponse response = PbUploadOffsetSet.UploadOffsetSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        int[] offsets = request.getParameter("offsets", new int[0]);
        PbUploadOffsetSet.UploadOffsetSetRequest.Builder builder = PbUploadOffsetSet.UploadOffsetSetRequest.newBuilder();
        for(int offset : offsets) {
            builder.addOffset(offset);
        }
        return builder.build().toByteArray();
    }

}
