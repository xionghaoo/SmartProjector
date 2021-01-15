package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbPartSwVer;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/08/28
 * @CmdId 210
 * @FunctionName part_sw_ver
 * @Description 部件软件版本号
 * @FileName URoUkitSmartPartSwVerFormatter.java
 * 
 **/
 public class URoUkitSmartPartSwVerFormatter extends URoUkitSmartCommandFormatter<PbPartSwVer.PartSwVerResponse> {

    private URoUkitSmartPartSwVerFormatter(){}

    public static final URoUkitSmartPartSwVerFormatter INSTANCE = new URoUkitSmartPartSwVerFormatter();

    @Override
    public URoResponse<PbPartSwVer.PartSwVerResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbPartSwVer.PartSwVerResponse data;
        try {
            PbPartSwVer.PartSwVerResponse response = PbPartSwVer.PartSwVerResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbPartSwVer.PartSwVerRequest.Builder builder = PbPartSwVer.PartSwVerRequest.newBuilder();
        builder.setPart(request.getParameter("part", 0));
        return builder.build().toByteArray();
    }

}
