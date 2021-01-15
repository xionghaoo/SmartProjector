package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbColorCalibrateCtrl;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1001
 * @FunctionName color_calibrate_ctrl
 * @Description 开启颜色校准
 * @FileName URoUkitSmartColorCalibrateCtrlFormatter.java
 * 
 **/
public class URoUkitSmartColorCalibrateCtrlFormatter extends URoUkitSmartCommandFormatter<PbColorCalibrateCtrl.ColorCalibrateCtrlResponse> {

    private URoUkitSmartColorCalibrateCtrlFormatter(){}

    public static final URoUkitSmartColorCalibrateCtrlFormatter INSTANCE = new URoUkitSmartColorCalibrateCtrlFormatter();

    @Override
    public URoResponse<PbColorCalibrateCtrl.ColorCalibrateCtrlResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbColorCalibrateCtrl.ColorCalibrateCtrlResponse data;
        try {
            PbColorCalibrateCtrl.ColorCalibrateCtrlResponse response = PbColorCalibrateCtrl.ColorCalibrateCtrlResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbColorCalibrateCtrl.ColorCalibrateCtrlRequest.Builder builder = PbColorCalibrateCtrl.ColorCalibrateCtrlRequest.newBuilder();
        builder.setRgb(request.getParameter("rgb", 0));
        builder.setOnoff(request.getParameter("onoff", false));
        return builder.build().toByteArray();
    }

}
