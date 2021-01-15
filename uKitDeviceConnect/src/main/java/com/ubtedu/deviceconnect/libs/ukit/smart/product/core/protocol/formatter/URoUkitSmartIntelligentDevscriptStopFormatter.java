package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevscriptStop;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/22
 * @CmdId 2001
 * @FunctionName intelligent_devscript_stop
 * @Description 打断正在执行的脚本
 * @FileName URoUkitSmartIntelligentDevscriptStopFormatter.java
 * 
 **/
public class URoUkitSmartIntelligentDevscriptStopFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevscriptStop.IntelligentDevscriptStopResponse> {

    private URoUkitSmartIntelligentDevscriptStopFormatter(){}

    public static final URoUkitSmartIntelligentDevscriptStopFormatter INSTANCE = new URoUkitSmartIntelligentDevscriptStopFormatter();

    @Override
    public URoResponse<PbIntelligentDevscriptStop.IntelligentDevscriptStopResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevscriptStop.IntelligentDevscriptStopResponse data;
        try {
            PbIntelligentDevscriptStop.IntelligentDevscriptStopResponse response = PbIntelligentDevscriptStop.IntelligentDevscriptStopResponse.parseFrom(bizData);
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
