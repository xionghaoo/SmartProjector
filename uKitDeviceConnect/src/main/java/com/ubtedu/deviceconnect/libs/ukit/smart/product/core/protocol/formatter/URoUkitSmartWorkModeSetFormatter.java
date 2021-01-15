package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbWorkModeSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/10/24
 * @CmdId 13
 * @FunctionName work_mode_set
 * @Description 设置当前工作方式
 * @FileName URoUkitSmartWorkModeSetFormatter.java
 * 
 **/
 public class URoUkitSmartWorkModeSetFormatter extends URoUkitSmartCommandFormatter<PbWorkModeSet.WorkModeSetResponse> {

    private URoUkitSmartWorkModeSetFormatter(){}

    public static final URoUkitSmartWorkModeSetFormatter INSTANCE = new URoUkitSmartWorkModeSetFormatter();

    @Override
    public URoResponse<PbWorkModeSet.WorkModeSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbWorkModeSet.WorkModeSetResponse data;
        try {
            PbWorkModeSet.WorkModeSetResponse response = PbWorkModeSet.WorkModeSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbWorkModeSet.WorkModeSetRequest.Builder builder = PbWorkModeSet.WorkModeSetRequest.newBuilder();
        builder.setWorkMode(request.getParameter("work_mode", 0));
        builder.setTime(request.getParameter("time", 0));
        int[] thresholds = request.getParameter("threshold", new int[0]);
        for(int threshold : thresholds) {
            builder.addThreshold(threshold);
        }
        int[] offsets = request.getParameter("offset", new int[0]);
        for(int offset : offsets) {
            builder.addOffset(offset);
        }
        return builder.build().toByteArray();
    }

}
