package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevScriptEventClear;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/22
 * @CmdId 2006
 * @FunctionName intelligent_dev_script_event_clear
 * @Description 脚本关联事件全部归零
 * @FileName URoUkitSmartIntelligentDevScriptEventClearFormatter.java
 * 
 **/
public class URoUkitSmartIntelligentDevScriptEventClearFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevScriptEventClear.IntelligentDevScriptEventClearResponse> {

    private URoUkitSmartIntelligentDevScriptEventClearFormatter(){}

    public static final URoUkitSmartIntelligentDevScriptEventClearFormatter INSTANCE = new URoUkitSmartIntelligentDevScriptEventClearFormatter();

    @Override
    public URoResponse<PbIntelligentDevScriptEventClear.IntelligentDevScriptEventClearResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevScriptEventClear.IntelligentDevScriptEventClearResponse data;
        try {
            PbIntelligentDevScriptEventClear.IntelligentDevScriptEventClearResponse response = PbIntelligentDevScriptEventClear.IntelligentDevScriptEventClearResponse.parseFrom(bizData);
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
