package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbBtInfoGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/07/01
 * @CmdId 350
 * @FunctionName bt_info_get
 * @Description 获取蓝牙连接信息
 * @FileName URoUkitSmartBtInfoGetFormatter.java
 * 
 **/
 public class URoUkitSmartBtInfoGetFormatter extends URoUkitSmartCommandFormatter<PbBtInfoGet.BtInfoGetResponse> {

    private URoUkitSmartBtInfoGetFormatter(){}

    public static final URoUkitSmartBtInfoGetFormatter INSTANCE = new URoUkitSmartBtInfoGetFormatter();

    @Override
    public URoResponse<PbBtInfoGet.BtInfoGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbBtInfoGet.BtInfoGetResponse data;
        try {
            PbBtInfoGet.BtInfoGetResponse response = PbBtInfoGet.BtInfoGetResponse.parseFrom(bizData);
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
