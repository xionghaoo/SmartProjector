package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevScriptEventUpdate;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/22
 * @CmdId 2005
 * @FunctionName intelligent_dev_script_event_update
 * @Description 设置脚本关联的事件
 * @FileName URoUkitSmartIntelligentDevScriptEventUpdateFormatter.java
 * 
 **/
public class URoUkitSmartIntelligentDevScriptEventUpdateFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevScriptEventUpdate.IntelligentDevScriptEventUpdateResponse> {

    private URoUkitSmartIntelligentDevScriptEventUpdateFormatter(){}

    public static final URoUkitSmartIntelligentDevScriptEventUpdateFormatter INSTANCE = new URoUkitSmartIntelligentDevScriptEventUpdateFormatter();

    @Override
    public URoResponse<PbIntelligentDevScriptEventUpdate.IntelligentDevScriptEventUpdateResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevScriptEventUpdate.IntelligentDevScriptEventUpdateResponse data;
        try {
            PbIntelligentDevScriptEventUpdate.IntelligentDevScriptEventUpdateResponse response = PbIntelligentDevScriptEventUpdate.IntelligentDevScriptEventUpdateResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        HashMap<String, String> paramt = request.getParameter("paramt", null);
        PbIntelligentDevScriptEventUpdate.IntelligentDevScriptEventUpdateRequest.Builder builder = PbIntelligentDevScriptEventUpdate.IntelligentDevScriptEventUpdateRequest.newBuilder();
        for(Map.Entry<String, String> entry : paramt.entrySet()) {
            PbIntelligentDevScriptEventUpdate.IntelligentDevScriptEventUpdateRequest.param_t.Builder paramBuilder = PbIntelligentDevScriptEventUpdate.IntelligentDevScriptEventUpdateRequest.param_t.newBuilder();
            paramBuilder.setName(entry.getKey());
            paramBuilder.setValue(entry.getValue());
            builder.addParam(paramBuilder);
        }
        return builder.build().toByteArray();
    }

}
