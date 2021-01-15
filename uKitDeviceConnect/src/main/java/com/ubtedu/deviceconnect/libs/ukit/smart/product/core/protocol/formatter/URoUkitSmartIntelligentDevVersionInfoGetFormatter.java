package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIntelligentDevVersionInfoGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/10/14
 * @CmdId 2520
 * @FunctionName intelligent_dev_version_info_get
 * @Description 获取设备版本号
 * @FileName URoUkitSmartIntelligentDevVersionInfoGetFormatter.java
 * 
 **/
 public class URoUkitSmartIntelligentDevVersionInfoGetFormatter extends URoUkitSmartCommandFormatter<PbIntelligentDevVersionInfoGet.IntelligentDevVersionInfoGetResponse> {

    private URoUkitSmartIntelligentDevVersionInfoGetFormatter(){}

    public static final URoUkitSmartIntelligentDevVersionInfoGetFormatter INSTANCE = new URoUkitSmartIntelligentDevVersionInfoGetFormatter();

    @Override
    public URoResponse<PbIntelligentDevVersionInfoGet.IntelligentDevVersionInfoGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIntelligentDevVersionInfoGet.IntelligentDevVersionInfoGetResponse data;
        try {
            PbIntelligentDevVersionInfoGet.IntelligentDevVersionInfoGetResponse response = PbIntelligentDevVersionInfoGet.IntelligentDevVersionInfoGetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbIntelligentDevVersionInfoGet.IntelligentDevVersionInfoGetRequest.Builder builder = PbIntelligentDevVersionInfoGet.IntelligentDevVersionInfoGetRequest.newBuilder();
        int dev = request.getParameter("dev", 0xff);
        builder.setDev(ByteString.copyFrom(new byte[]{(byte)dev}));
        return builder.build().toByteArray();
    }

}
