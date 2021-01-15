package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbSwVer;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 4
 * @FunctionName sw_ver
 * @Description 获取软件版本号
 * @FileName URoUkitSmartSwVerFormatter.java
 * 
 **/
public class URoUkitSmartSwVerFormatter extends URoUkitSmartCommandFormatter<PbSwVer.SwVerResponse> {

    private URoUkitSmartSwVerFormatter(){}

    public static final URoUkitSmartSwVerFormatter INSTANCE = new URoUkitSmartSwVerFormatter();

    @Override
    public URoResponse<PbSwVer.SwVerResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbSwVer.SwVerResponse data;
        try {
            PbSwVer.SwVerResponse response = PbSwVer.SwVerResponse.parseFrom(bizData);
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
