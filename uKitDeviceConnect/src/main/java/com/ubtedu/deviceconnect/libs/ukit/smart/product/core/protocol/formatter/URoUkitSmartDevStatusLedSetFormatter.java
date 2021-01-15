package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbDevStatusLedSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 22
 * @FunctionName dev_status_led_set
 * @Description 设备状态灯设置
 * @FileName URoUkitSmartDevStatusLedSetFormatter.java
 * 
 **/
public class URoUkitSmartDevStatusLedSetFormatter extends URoUkitSmartCommandFormatter<PbDevStatusLedSet.DevStatusLedSetResponse> {

    private URoUkitSmartDevStatusLedSetFormatter(){}

    public static final URoUkitSmartDevStatusLedSetFormatter INSTANCE = new URoUkitSmartDevStatusLedSetFormatter();

    @Override
    public URoResponse<PbDevStatusLedSet.DevStatusLedSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbDevStatusLedSet.DevStatusLedSetResponse data;
        try {
            PbDevStatusLedSet.DevStatusLedSetResponse response = PbDevStatusLedSet.DevStatusLedSetResponse.parseFrom(bizData);
            // TODO check the response
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbDevStatusLedSet.DevStatusLedSetRequest.Builder builder = PbDevStatusLedSet.DevStatusLedSetRequest.newBuilder();
        /* TODO set request params to pb
        builder.setPath(request.getParameter("path", ""));
        */
        return builder.build().toByteArray();
    }

}
