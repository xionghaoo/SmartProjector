package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevChangeSwitchSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/09/27
 * @CmdId 2512
 * @FunctionName intelligent_dev_change_switch_set
 * @Description 外设变更功能开关
 * @FileName URoUkitSmartIntelligentDevChangeSwitchSetFormatter.java
 * 
 **/
 public class URoUkitSmartIntelligentDevChangeSwitchSetFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevChangeSwitchSet.IntelligentDevChangeSwitchSetResponse> {

    private URoUkitSmartIntelligentDevChangeSwitchSetFormatter(){}

    public static final URoUkitSmartIntelligentDevChangeSwitchSetFormatter INSTANCE = new URoUkitSmartIntelligentDevChangeSwitchSetFormatter();

    @Override
    public URoResponse<PbIntelligentDevChangeSwitchSet.IntelligentDevChangeSwitchSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevChangeSwitchSet.IntelligentDevChangeSwitchSetResponse data;
        try {
            PbIntelligentDevChangeSwitchSet.IntelligentDevChangeSwitchSetResponse response = PbIntelligentDevChangeSwitchSet.IntelligentDevChangeSwitchSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbIntelligentDevChangeSwitchSet.IntelligentDevChangeSwitchSetRequest.Builder builder = PbIntelligentDevChangeSwitchSet.IntelligentDevChangeSwitchSetRequest.newBuilder();
        builder.setStatus(request.getParameter("status", false) ? 1 : 0);
        return builder.build().toByteArray();
    }

}
