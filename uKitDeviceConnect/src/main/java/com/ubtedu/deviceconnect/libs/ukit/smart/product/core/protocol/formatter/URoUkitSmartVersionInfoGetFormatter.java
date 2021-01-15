package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbVersionInfoGet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/07/01
 * @CmdId 403
 * @FunctionName version_info_get
 * @Description 获取设备版本号
 * @FileName URoUkitSmartVersionInfoGetFormatter.java
 * 
 **/
 public class URoUkitSmartVersionInfoGetFormatter extends URoUkitSmartCommandFormatter<PbVersionInfoGet.VersionInfoGetResponse> {

    private URoUkitSmartVersionInfoGetFormatter(){}

    public static final URoUkitSmartVersionInfoGetFormatter INSTANCE = new URoUkitSmartVersionInfoGetFormatter();

    @Override
    public URoResponse<PbVersionInfoGet.VersionInfoGetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbVersionInfoGet.VersionInfoGetResponse data;
        try {
            PbVersionInfoGet.VersionInfoGetResponse response = PbVersionInfoGet.VersionInfoGetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbVersionInfoGet.VersionInfoGetRequest.Builder builder = PbVersionInfoGet.VersionInfoGetRequest.newBuilder();
        int dev = request.getParameter("dev", 0xff);
        builder.setDev(ByteString.copyFrom(new byte[]{(byte)dev}));
        return builder.build().toByteArray();
    }

}
