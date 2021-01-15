package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbPtVer;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 5
 * @FunctionName pt_ver
 * @Description 协议版本号
 * @FileName URoUkitSmartPtVerFormatter.java
 * 
 **/
public class URoUkitSmartPtVerFormatter extends URoUkitSmartCommandFormatter<PbPtVer.PtVerResponse> {

    private URoUkitSmartPtVerFormatter(){}

    public static final URoUkitSmartPtVerFormatter INSTANCE = new URoUkitSmartPtVerFormatter();

    @Override
    public URoResponse<PbPtVer.PtVerResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbPtVer.PtVerResponse data;
        try {
            PbPtVer.PtVerResponse response = PbPtVer.PtVerResponse.parseFrom(bizData);
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
