package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLcdSensorDisplay;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/06/22
 * @CmdId 1010
 * @FunctionName lcd_sensor_display
 * @Description 显示传感器数值
 * @FileName URoUkitSmartLcdSensorDisplayFormatter.java
 * 
 **/
 public class URoUkitSmartLcdSensorDisplayFormatter extends URoUkitSmartCommandFormatter<PbLcdSensorDisplay.LcdSensorDisplayResponse> {

    private URoUkitSmartLcdSensorDisplayFormatter(){}

    public static final URoUkitSmartLcdSensorDisplayFormatter INSTANCE = new URoUkitSmartLcdSensorDisplayFormatter();

    @Override
    public URoResponse<PbLcdSensorDisplay.LcdSensorDisplayResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbLcdSensorDisplay.LcdSensorDisplayResponse data;
        try {
            PbLcdSensorDisplay.LcdSensorDisplayResponse response = PbLcdSensorDisplay.LcdSensorDisplayResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbLcdSensorDisplay.LcdSensorDisplayRequest.Builder builder = PbLcdSensorDisplay.LcdSensorDisplayRequest.newBuilder();
        builder.setOpt(request.getParameter("opt", 0));
        builder.setDev(request.getParameter("dev", 0));
        return builder.build().toByteArray();
    }

}
