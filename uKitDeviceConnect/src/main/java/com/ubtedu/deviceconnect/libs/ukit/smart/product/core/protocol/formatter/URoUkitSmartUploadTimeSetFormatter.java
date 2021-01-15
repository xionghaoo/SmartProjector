package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbUploadTimeSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/10/24
 * @CmdId 14
 * @FunctionName upload_time_set
 * @Description 设置定时上报的时间间隔
 * @FileName URoUkitSmartUploadTimeSetFormatter.java
 * 
 **/
 public class URoUkitSmartUploadTimeSetFormatter extends URoUkitSmartCommandFormatter<PbUploadTimeSet.UploadTimeSetResponse> {

    private URoUkitSmartUploadTimeSetFormatter(){}

    public static final URoUkitSmartUploadTimeSetFormatter INSTANCE = new URoUkitSmartUploadTimeSetFormatter();

    @Override
    public URoResponse<PbUploadTimeSet.UploadTimeSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbUploadTimeSet.UploadTimeSetResponse data;
        try {
            PbUploadTimeSet.UploadTimeSetResponse response = PbUploadTimeSet.UploadTimeSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbUploadTimeSet.UploadTimeSetRequest.Builder builder = PbUploadTimeSet.UploadTimeSetRequest.newBuilder();
        builder.setTime(request.getParameter("time", 0));
        return builder.build().toByteArray();
    }

}
