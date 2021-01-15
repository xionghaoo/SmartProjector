package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevDebugSwitchSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/09/19
 * @CmdId 2508
 * @FunctionName intelligent_dev_debug_switch_set
 * @Description 设置调试信息开关
 * @FileName URoUkitSmartIntelligentDevDebugSwitchSetFormatter.java
 * 
 **/
 public class URoUkitSmartIntelligentDevDebugSwitchSetFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevDebugSwitchSet.IntelligentDevDebugSwitchSetResponse> {

    private URoUkitSmartIntelligentDevDebugSwitchSetFormatter(){}

    public static final URoUkitSmartIntelligentDevDebugSwitchSetFormatter INSTANCE = new URoUkitSmartIntelligentDevDebugSwitchSetFormatter();

    @Override
    public URoResponse<PbIntelligentDevDebugSwitchSet.IntelligentDevDebugSwitchSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevDebugSwitchSet.IntelligentDevDebugSwitchSetResponse data;
        try {
            PbIntelligentDevDebugSwitchSet.IntelligentDevDebugSwitchSetResponse response = PbIntelligentDevDebugSwitchSet.IntelligentDevDebugSwitchSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbIntelligentDevDebugSwitchSet.IntelligentDevDebugSwitchSetRequest.Builder builder = PbIntelligentDevDebugSwitchSet.IntelligentDevDebugSwitchSetRequest.newBuilder();
        builder.setStatus(request.getParameter("status", -1));
        return builder.build().toByteArray();
    }

}
