package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLightStdvalueSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1001
 * @FunctionName light_stdvalue_set
 * @Description 设置照度标定lux值
 * @FileName URoUkitSmartLightStdvalueSetFormatter.java
 * 
 **/
public class URoUkitSmartLightStdvalueSetFormatter extends URoUkitSmartCommandFormatter<PbLightStdvalueSet.LightStdvalueSetResponse> {

    private URoUkitSmartLightStdvalueSetFormatter(){}

    public static final URoUkitSmartLightStdvalueSetFormatter INSTANCE = new URoUkitSmartLightStdvalueSetFormatter();

    @Override
    public URoResponse<PbLightStdvalueSet.LightStdvalueSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbLightStdvalueSet.LightStdvalueSetResponse data;
        try {
            PbLightStdvalueSet.LightStdvalueSetResponse response = PbLightStdvalueSet.LightStdvalueSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbLightStdvalueSet.LightStdvalueSetRequest.Builder builder = PbLightStdvalueSet.LightStdvalueSetRequest.newBuilder();
        builder.setStdvalue(request.getParameter("stdvalue", 0));
        return builder.build().toByteArray();
    }

}
