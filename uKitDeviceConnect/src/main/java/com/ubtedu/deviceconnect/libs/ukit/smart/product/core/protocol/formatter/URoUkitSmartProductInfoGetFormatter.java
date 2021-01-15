package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbProductInfoGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/07/01
 * @CmdId 402
 * @FunctionName product_info_get
 * @Description 获取产品信息
 * @FileName URoUkitSmartProductInfoGetFormatter.java
 * 
 **/
 public class URoUkitSmartProductInfoGetFormatter extends URoUkitSmartCommandFormatter<PbProductInfoGet.ProductInfoGetResponse> {

    private URoUkitSmartProductInfoGetFormatter(){}

    public static final URoUkitSmartProductInfoGetFormatter INSTANCE = new URoUkitSmartProductInfoGetFormatter();

    @Override
    public URoResponse<PbProductInfoGet.ProductInfoGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbProductInfoGet.ProductInfoGetResponse data;
        try {
            PbProductInfoGet.ProductInfoGetResponse response = PbProductInfoGet.ProductInfoGetResponse.parseFrom(bizData);
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
