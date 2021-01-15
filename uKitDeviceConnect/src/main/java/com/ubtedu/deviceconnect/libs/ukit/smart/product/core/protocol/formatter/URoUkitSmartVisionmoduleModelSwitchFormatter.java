package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVisionmoduleModelSwitch;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/07/01
 * @CmdId 1008
 * @FunctionName visionmodule_model_switch
 * @Description 模型切换/开关
 * @FileName URoUkitSmartVisionmoduleModelSwitchFormatter.java
 * 
 **/
 public class URoUkitSmartVisionmoduleModelSwitchFormatter extends URoUkitSmartCommandFormatter<PbVisionmoduleModelSwitch.VisionmoduleModelSwitchResponse> {

    private URoUkitSmartVisionmoduleModelSwitchFormatter(){}

    public static final URoUkitSmartVisionmoduleModelSwitchFormatter INSTANCE = new URoUkitSmartVisionmoduleModelSwitchFormatter();

    @Override
    public URoResponse<PbVisionmoduleModelSwitch.VisionmoduleModelSwitchResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVisionmoduleModelSwitch.VisionmoduleModelSwitchResponse data;
        try {
            PbVisionmoduleModelSwitch.VisionmoduleModelSwitchResponse response = PbVisionmoduleModelSwitch.VisionmoduleModelSwitchResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbVisionmoduleModelSwitch.VisionmoduleModelSwitchRequest.Builder builder = PbVisionmoduleModelSwitch.VisionmoduleModelSwitchRequest.newBuilder();
        builder.setModel(request.getParameter("model", 0));
        return builder.build().toByteArray();
    }

}
