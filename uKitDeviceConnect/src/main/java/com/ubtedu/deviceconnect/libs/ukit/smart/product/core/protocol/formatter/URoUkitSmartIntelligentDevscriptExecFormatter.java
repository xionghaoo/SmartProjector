package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevscriptExec;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/22
 * @CmdId 2000
 * @FunctionName intelligent_devscript_exec
 * @Description 运行指定脚本
 * @FileName URoUkitSmartIntelligentDevscriptExecFormatter.java
 * 
 **/
public class URoUkitSmartIntelligentDevscriptExecFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevscriptExec.IntelligentDevscriptExecResponse> {

    private URoUkitSmartIntelligentDevscriptExecFormatter(){}

    public static final URoUkitSmartIntelligentDevscriptExecFormatter INSTANCE = new URoUkitSmartIntelligentDevscriptExecFormatter();

    @Override
    public URoResponse<PbIntelligentDevscriptExec.IntelligentDevscriptExecResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevscriptExec.IntelligentDevscriptExecResponse data;
        try {
            PbIntelligentDevscriptExec.IntelligentDevscriptExecResponse response = PbIntelligentDevscriptExec.IntelligentDevscriptExecResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbIntelligentDevscriptExec.IntelligentDevscriptExecRequest.Builder builder = PbIntelligentDevscriptExec.IntelligentDevscriptExecRequest.newBuilder();
        builder.setFileName(request.getParameter("file_name", ""));
        return builder.build().toByteArray();
    }

}
