package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbInfraredOnoffGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 1002
 * @FunctionName infrared_onoff_get
 * @Description 读取红外开关量
 * @FileName URoUkitSmartInfraredOnoffGetFormatter.java
 * 
 **/
public class URoUkitSmartInfraredOnoffGetFormatter extends URoUkitSmartCommandFormatter<PbInfraredOnoffGet.InfraredOnoffGetResponse> {

    private URoUkitSmartInfraredOnoffGetFormatter(){}

    public static final URoUkitSmartInfraredOnoffGetFormatter INSTANCE = new URoUkitSmartInfraredOnoffGetFormatter();

    @Override
    public URoResponse<PbInfraredOnoffGet.InfraredOnoffGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbInfraredOnoffGet.InfraredOnoffGetResponse data;
        try {
            PbInfraredOnoffGet.InfraredOnoffGetResponse response = PbInfraredOnoffGet.InfraredOnoffGetResponse.parseFrom(bizData);
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
