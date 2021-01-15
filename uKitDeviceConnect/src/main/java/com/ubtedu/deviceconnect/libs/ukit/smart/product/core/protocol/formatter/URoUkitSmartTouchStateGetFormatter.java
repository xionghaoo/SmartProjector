package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbTouchStateGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId: 1004
 * @FunctionName：touch_state_get
 * @Description：读取触碰按下状态
 * @FileName: URoUkitSmartTouchStateGetFormatter.java
 * 
 **/
public class URoUkitSmartTouchStateGetFormatter extends URoUkitSmartCommandFormatter<PbTouchStateGet.TouchStateGetResponse> {

    private URoUkitSmartTouchStateGetFormatter(){}

    public static final URoUkitSmartTouchStateGetFormatter INSTANCE = new URoUkitSmartTouchStateGetFormatter();

    @Override
    public URoResponse<PbTouchStateGet.TouchStateGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbTouchStateGet.TouchStateGetResponse data;
        try {
            PbTouchStateGet.TouchStateGetResponse response = PbTouchStateGet.TouchStateGetResponse.parseFrom(bizData);
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
