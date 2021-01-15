package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLedBeltOffSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/04/30
 * @CmdId 1004
 * @FunctionName led_belt_off_set
 * @Description 灯带关闭设置
 * @FileName URoUkitSmartLedBeltOffSetFormatter.java
 * 
 **/
 public class URoUkitSmartLedBeltOffSetFormatter extends URoUkitSmartCommandFormatter<PbLedBeltOffSet.LedBeltOffSetResponse> {

    private URoUkitSmartLedBeltOffSetFormatter(){}

    public static final URoUkitSmartLedBeltOffSetFormatter INSTANCE = new URoUkitSmartLedBeltOffSetFormatter();

    @Override
    public URoResponse<PbLedBeltOffSet.LedBeltOffSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbLedBeltOffSet.LedBeltOffSetResponse data;
        try {
            PbLedBeltOffSet.LedBeltOffSetResponse response = PbLedBeltOffSet.LedBeltOffSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbLedBeltOffSet.LedBeltOffSetRequest.Builder builder = PbLedBeltOffSet.LedBeltOffSetRequest.newBuilder();
        int[] ports = request.getParameter("ports", new int[0]);
        for(int port : ports) {
            builder.addPort(port);
        }
        return builder.build().toByteArray();
    }

}
