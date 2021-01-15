package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevStreamSwitchSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/02/18
 * @CmdId 2529
 * @FunctionName intelligent_dev_stream_switch_set
 * @Description 数据流开关
 * @FileName URoUkitSmartIntelligentDevStreamSwitchSetFormatter.java
 * 
 **/
 public class URoUkitSmartIntelligentDevStreamSwitchSetFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevStreamSwitchSet.IntelligentDevStreamSwitchSetResponse> {

    private URoUkitSmartIntelligentDevStreamSwitchSetFormatter(){}

    public static final URoUkitSmartIntelligentDevStreamSwitchSetFormatter INSTANCE = new URoUkitSmartIntelligentDevStreamSwitchSetFormatter();

    @Override
    public URoResponse<PbIntelligentDevStreamSwitchSet.IntelligentDevStreamSwitchSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevStreamSwitchSet.IntelligentDevStreamSwitchSetResponse data;
        try {
            PbIntelligentDevStreamSwitchSet.IntelligentDevStreamSwitchSetResponse response = PbIntelligentDevStreamSwitchSet.IntelligentDevStreamSwitchSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbIntelligentDevStreamSwitchSet.IntelligentDevStreamSwitchSetRequest.Builder builder = PbIntelligentDevStreamSwitchSet.IntelligentDevStreamSwitchSetRequest.newBuilder();
        builder.setStatus(request.getParameter("status", -1));
        return builder.build().toByteArray();
    }

}
