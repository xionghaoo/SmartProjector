package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevFaultClear;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/09/19
 * @CmdId 2501
 * @FunctionName intelligent_dev_fault_clear
 * @Description 清除智能设备异常
 * @FileName URoUkitSmartIntelligentDevFaultClearFormatter.java
 * 
 **/
 public class URoUkitSmartIntelligentDevFaultClearFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevFaultClear.IntelligentDevFaultClearResponse> {

    private URoUkitSmartIntelligentDevFaultClearFormatter(){}

    public static final URoUkitSmartIntelligentDevFaultClearFormatter INSTANCE = new URoUkitSmartIntelligentDevFaultClearFormatter();

    @Override
    public URoResponse<PbIntelligentDevFaultClear.IntelligentDevFaultClearResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevFaultClear.IntelligentDevFaultClearResponse data;
        try {
            PbIntelligentDevFaultClear.IntelligentDevFaultClearResponse response = PbIntelligentDevFaultClear.IntelligentDevFaultClearResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbIntelligentDevFaultClear.IntelligentDevFaultClearRequest.Builder builder = PbIntelligentDevFaultClear.IntelligentDevFaultClearRequest.newBuilder();
        int dev = request.getParameter("dev", 0xff);
        builder.setDev(ByteString.copyFrom(new byte[]{(byte)dev}));
        return builder.build().toByteArray();
    }

}
