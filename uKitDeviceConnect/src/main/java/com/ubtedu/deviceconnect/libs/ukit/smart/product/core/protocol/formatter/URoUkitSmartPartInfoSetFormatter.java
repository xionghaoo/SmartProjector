package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbPartInfoSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/08/28
 * @CmdId 205
 * @FunctionName part_info_set
 * @Description 部件升级
 * @FileName URoUkitSmartPartInfoSetFormatter.java
 * 
 **/
 public class URoUkitSmartPartInfoSetFormatter extends URoUkitSmartCommandFormatter<PbPartInfoSet.PartInfoSetResponse> {

    private URoUkitSmartPartInfoSetFormatter(){}

    public static final URoUkitSmartPartInfoSetFormatter INSTANCE = new URoUkitSmartPartInfoSetFormatter();

    @Override
    public URoResponse<PbPartInfoSet.PartInfoSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbPartInfoSet.PartInfoSetResponse data;
        try {
            PbPartInfoSet.PartInfoSetResponse response = PbPartInfoSet.PartInfoSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbPartInfoSet.PartInfoSetRequest.Builder builder = PbPartInfoSet.PartInfoSetRequest.newBuilder();
        builder.setPart(request.getParameter("part", 0));
        builder.setPackageUrl(request.getParameter("packageUrl", ""));
        builder.setPackageMd5(request.getParameter("packageMd5", ""));
        builder.setPackageSize(request.getParameter("packageSize", 0));
        return builder.build().toByteArray();
    }

}
