package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbTouchTypeGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId: 1001
 * @FunctionName：touch_type_get
 * @Description：读取触碰按下类型
 * @FileName: URoUkitSmartTouchTypeGetFormatter.java
 * 
 **/
public class URoUkitSmartTouchTypeGetFormatter extends URoUkitSmartCommandFormatter<PbTouchTypeGet.TouchTypeGetResponse> {

    private URoUkitSmartTouchTypeGetFormatter(){}

    public static final URoUkitSmartTouchTypeGetFormatter INSTANCE = new URoUkitSmartTouchTypeGetFormatter();

    @Override
    public URoResponse<PbTouchTypeGet.TouchTypeGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbTouchTypeGet.TouchTypeGetResponse data;
        try {
            PbTouchTypeGet.TouchTypeGetResponse response = PbTouchTypeGet.TouchTypeGetResponse.parseFrom(bizData);
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
