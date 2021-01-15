package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbHwVer;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 3
 * @FunctionName hw_ver
 * @Description 获取硬件版本号
 * @FileName URoUkitSmartHwVerFormatter.java
 * 
 **/
public class URoUkitSmartHwVerFormatter extends URoUkitSmartCommandFormatter<PbHwVer.HwVerResponse> {

    private URoUkitSmartHwVerFormatter(){}

    public static final URoUkitSmartHwVerFormatter INSTANCE = new URoUkitSmartHwVerFormatter();

    @Override
    public URoResponse<PbHwVer.HwVerResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbHwVer.HwVerResponse data;
        try {
            PbHwVer.HwVerResponse response = PbHwVer.HwVerResponse.parseFrom(bizData);
            URoLogUtils.d("HwVer: %s", response.getHwVer());
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
