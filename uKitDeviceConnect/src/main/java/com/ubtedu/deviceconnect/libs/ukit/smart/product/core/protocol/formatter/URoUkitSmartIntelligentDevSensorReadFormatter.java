package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevSensorRead;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/22
 * @CmdId 2505
 * @FunctionName intelligent_dev_sensor_read
 * @Description 读取指定传感器信息
 * @FileName URoUkitSmartIntelligentDevSensorReadFormatter.java
 * 
 **/
public class URoUkitSmartIntelligentDevSensorReadFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevSensorRead.IntelligentDevSensorReadResponse> {

    private URoUkitSmartIntelligentDevSensorReadFormatter(){}

    public static final URoUkitSmartIntelligentDevSensorReadFormatter INSTANCE = new URoUkitSmartIntelligentDevSensorReadFormatter();

    @Override
    public URoResponse<PbIntelligentDevSensorRead.IntelligentDevSensorReadResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevSensorRead.IntelligentDevSensorReadResponse data;
        try {
            PbIntelligentDevSensorRead.IntelligentDevSensorReadResponse response = PbIntelligentDevSensorRead.IntelligentDevSensorReadResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbIntelligentDevSensorRead.IntelligentDevSensorReadRequest.Builder builder = PbIntelligentDevSensorRead.IntelligentDevSensorReadRequest.newBuilder();
        /* TODO set request params to pb
        builder.setPath(request.getParameter("path", ""));
        */
        return builder.build().toByteArray();
    }

}
