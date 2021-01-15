package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevscriptParamClear;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/22
 * @CmdId 2004
 * @FunctionName intelligent_devscript_param_clear
 * @Description 脚本关联变量全部归零
 * @FileName URoUkitSmartIntelligentDevscriptParamClearFormatter.java
 * 
 **/
public class URoUkitSmartIntelligentDevscriptParamClearFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevscriptParamClear.IntelligentDevscriptParamClearResponse> {

    private URoUkitSmartIntelligentDevscriptParamClearFormatter(){}

    public static final URoUkitSmartIntelligentDevscriptParamClearFormatter INSTANCE = new URoUkitSmartIntelligentDevscriptParamClearFormatter();

    @Override
    public URoResponse<PbIntelligentDevscriptParamClear.IntelligentDevscriptParamClearResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevscriptParamClear.IntelligentDevscriptParamClearResponse data;
        try {
            PbIntelligentDevscriptParamClear.IntelligentDevscriptParamClearResponse response = PbIntelligentDevscriptParamClear.IntelligentDevscriptParamClearResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        return EMPTY_REQUEST_DATA;
    }

}
