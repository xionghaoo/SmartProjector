package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevComboOff;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/22
 * @CmdId 1009
 * @FunctionName intelligent_dev_combo_off
 * @Description 停止外设运行
 * @FileName URoUkitSmartIntelligentDevComboOffFormatter.java
 * 
 **/
public class URoUkitSmartIntelligentDevComboOffFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevComboOff.IntelligentDevComboOffResponse> {

    private URoUkitSmartIntelligentDevComboOffFormatter(){}

    public static final URoUkitSmartIntelligentDevComboOffFormatter INSTANCE = new URoUkitSmartIntelligentDevComboOffFormatter();

    @Override
    public URoResponse<PbIntelligentDevComboOff.IntelligentDevComboOffResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevComboOff.IntelligentDevComboOffResponse data;
        try {
            PbIntelligentDevComboOff.IntelligentDevComboOffResponse response = PbIntelligentDevComboOff.IntelligentDevComboOffResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbIntelligentDevComboOff.IntelligentDevComboOffRequest.Builder builder = PbIntelligentDevComboOff.IntelligentDevComboOffRequest.newBuilder();
        int dev = request.getParameter("dev", 0xff);
        builder.setDev(ByteString.copyFrom(new byte[]{(byte)dev}));
        return builder.build().toByteArray();
    }

}
