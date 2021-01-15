package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbWorkModeGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/10/24
 * @CmdId 12
 * @FunctionName work_mode_get
 * @Description 获取当前工作方式
 * @FileName URoUkitSmartWorkModeGetFormatter.java
 * 
 **/
 public class URoUkitSmartWorkModeGetFormatter extends URoUkitSmartCommandFormatter<PbWorkModeGet.WorkModeGetResponse> {

    private URoUkitSmartWorkModeGetFormatter(){}

    public static final URoUkitSmartWorkModeGetFormatter INSTANCE = new URoUkitSmartWorkModeGetFormatter();

    @Override
    public URoResponse<PbWorkModeGet.WorkModeGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbWorkModeGet.WorkModeGetResponse data;
        try {
            PbWorkModeGet.WorkModeGetResponse response = PbWorkModeGet.WorkModeGetResponse.parseFrom(bizData);
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
