package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbPcLinkInfoGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/07/01
 * @CmdId 400
 * @FunctionName pc_link_info_get
 * @Description 获取PC连接信息
 * @FileName URoUkitSmartPcLinkInfoGetFormatter.java
 * 
 **/
 public class URoUkitSmartPcLinkInfoGetFormatter extends URoUkitSmartCommandFormatter<PbPcLinkInfoGet.PcLinkInfoGetResponse> {

    private URoUkitSmartPcLinkInfoGetFormatter(){}

    public static final URoUkitSmartPcLinkInfoGetFormatter INSTANCE = new URoUkitSmartPcLinkInfoGetFormatter();

    @Override
    public URoResponse<PbPcLinkInfoGet.PcLinkInfoGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbPcLinkInfoGet.PcLinkInfoGetResponse data;
        try {
            PbPcLinkInfoGet.PcLinkInfoGetResponse response = PbPcLinkInfoGet.PcLinkInfoGetResponse.parseFrom(bizData);
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
