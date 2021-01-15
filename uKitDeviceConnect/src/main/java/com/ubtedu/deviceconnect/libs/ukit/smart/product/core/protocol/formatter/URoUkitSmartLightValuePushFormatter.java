package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLightValuePush;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/10/24
 * @CmdId 1004
 * @FunctionName light_value_push
 * @Description lux值上报
 * @FileName URoUkitSmartLightValuePushFormatter.java
 * 
 **/
 public class URoUkitSmartLightValuePushFormatter extends URoUkitSmartCommandFormatter<PbLightValuePush.LightValuePushResponse> {

    private URoUkitSmartLightValuePushFormatter(){}

    public static final URoUkitSmartLightValuePushFormatter INSTANCE = new URoUkitSmartLightValuePushFormatter();

    @Override
    public URoResponse<PbLightValuePush.LightValuePushResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbLightValuePush.LightValuePushResponse data;
        try {
            PbLightValuePush.LightValuePushResponse response = PbLightValuePush.LightValuePushResponse.parseFrom(bizData);
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
