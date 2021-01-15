package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevRedirectSwitchSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/02/18
 * @CmdId 2528
 * @FunctionName intelligent_dev_redirect_switch_set
 * @Description 重定向信息开关
 * @FileName URoUkitSmartIntelligentDevRedirectSwitchSetFormatter.java
 * 
 **/
 public class URoUkitSmartIntelligentDevRedirectSwitchSetFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevRedirectSwitchSet.IntelligentDevRedirectSwitchSetResponse> {

    private URoUkitSmartIntelligentDevRedirectSwitchSetFormatter(){}

    public static final URoUkitSmartIntelligentDevRedirectSwitchSetFormatter INSTANCE = new URoUkitSmartIntelligentDevRedirectSwitchSetFormatter();

    @Override
    public URoResponse<PbIntelligentDevRedirectSwitchSet.IntelligentDevRedirectSwitchSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevRedirectSwitchSet.IntelligentDevRedirectSwitchSetResponse data;
        try {
            PbIntelligentDevRedirectSwitchSet.IntelligentDevRedirectSwitchSetResponse response = PbIntelligentDevRedirectSwitchSet.IntelligentDevRedirectSwitchSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbIntelligentDevRedirectSwitchSet.IntelligentDevRedirectSwitchSetRequest.Builder builder = PbIntelligentDevRedirectSwitchSet.IntelligentDevRedirectSwitchSetRequest.newBuilder();
        builder.setStatus(request.getParameter("status", -1));
        builder.setComType(request.getParameter("com_type", -1));
        return builder.build().toByteArray();
    }

}
