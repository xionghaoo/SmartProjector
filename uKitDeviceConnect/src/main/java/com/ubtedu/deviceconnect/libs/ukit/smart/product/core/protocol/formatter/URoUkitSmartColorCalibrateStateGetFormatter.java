package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbColorCalibrateStateGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1002
 * @FunctionName color_calibrate_state_get
 * @Description 查询颜色校准状态
 * @FileName URoUkitSmartColorCalibrateStateGetFormatter.java
 * 
 **/
public class URoUkitSmartColorCalibrateStateGetFormatter extends URoUkitSmartCommandFormatter<PbColorCalibrateStateGet.ColorCalibrateStateGetResponse> {

    private URoUkitSmartColorCalibrateStateGetFormatter(){}

    public static final URoUkitSmartColorCalibrateStateGetFormatter INSTANCE = new URoUkitSmartColorCalibrateStateGetFormatter();

    @Override
    public URoResponse<PbColorCalibrateStateGet.ColorCalibrateStateGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbColorCalibrateStateGet.ColorCalibrateStateGetResponse data;
        try {
            PbColorCalibrateStateGet.ColorCalibrateStateGetResponse response = PbColorCalibrateStateGet.ColorCalibrateStateGetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbColorCalibrateStateGet.ColorCalibrateStateGetRequest.Builder builder = PbColorCalibrateStateGet.ColorCalibrateStateGetRequest.newBuilder();
        builder.setRgb(request.getParameter("rgb", 0));
        return builder.build().toByteArray();
    }

}
