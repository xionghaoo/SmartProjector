package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevscriptParamUpdate;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/22
 * @CmdId 2003
 * @FunctionName intelligent_devscript_param_update
 * @Description 设置脚本关联的变量
 * @FileName URoUkitSmartIntelligentDevscriptParamUpdateFormatter.java
 * 
 **/
public class URoUkitSmartIntelligentDevscriptParamUpdateFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevscriptParamUpdate.IntelligentDevscriptParamUpdateResponse> {

    private URoUkitSmartIntelligentDevscriptParamUpdateFormatter(){}

    public static final URoUkitSmartIntelligentDevscriptParamUpdateFormatter INSTANCE = new URoUkitSmartIntelligentDevscriptParamUpdateFormatter();

    @Override
    public URoResponse<PbIntelligentDevscriptParamUpdate.IntelligentDevscriptParamUpdateResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevscriptParamUpdate.IntelligentDevscriptParamUpdateResponse data;
        try {
            PbIntelligentDevscriptParamUpdate.IntelligentDevscriptParamUpdateResponse response = PbIntelligentDevscriptParamUpdate.IntelligentDevscriptParamUpdateResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        HashMap<String, String> paramt = request.getParameter("paramt", null);
        PbIntelligentDevscriptParamUpdate.IntelligentDevscriptParamUpdateRequest.Builder builder = PbIntelligentDevscriptParamUpdate.IntelligentDevscriptParamUpdateRequest.newBuilder();
        for(Map.Entry<String, String> entry : paramt.entrySet()) {
            PbIntelligentDevscriptParamUpdate.IntelligentDevscriptParamUpdateRequest.param_t.Builder paramBuilder = PbIntelligentDevscriptParamUpdate.IntelligentDevscriptParamUpdateRequest.param_t.newBuilder();
            paramBuilder.setName(entry.getKey());
            paramBuilder.setValue(entry.getValue());
            builder.addParamt(paramBuilder);
        }
        return builder.build().toByteArray();
    }

}
