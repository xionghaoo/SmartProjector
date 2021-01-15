package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevInfoGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/22
 * @CmdId 2504
 * @FunctionName intelligent_dev_info_get
 * @Description 获取主板信息
 * @FileName URoUkitSmartIntelligentDevInfoGetFormatter.java
 * 
 **/
public class URoUkitSmartIntelligentDevInfoGetFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevInfoGet.IntelligentDevInfoGetResponse> {

    private URoUkitSmartIntelligentDevInfoGetFormatter(){}

    public static final URoUkitSmartIntelligentDevInfoGetFormatter INSTANCE = new URoUkitSmartIntelligentDevInfoGetFormatter();

    @Override
    public URoResponse<PbIntelligentDevInfoGet.IntelligentDevInfoGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevInfoGet.IntelligentDevInfoGetResponse data;
        try {
            PbIntelligentDevInfoGet.IntelligentDevInfoGetResponse response = PbIntelligentDevInfoGet.IntelligentDevInfoGetResponse.parseFrom(bizData);
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
