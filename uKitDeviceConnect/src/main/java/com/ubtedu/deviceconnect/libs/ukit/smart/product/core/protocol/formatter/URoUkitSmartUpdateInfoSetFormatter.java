package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbUpdateInfoSet;

import java.util.HashSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/22
 * @CmdId 256
 * @FunctionName update_info_set
 * @Description 设置升级信息
 * @FileName URoUkitSmartUpdateInfoSetFormatter.java
 * 
 **/
public class URoUkitSmartUpdateInfoSetFormatter extends URoUkitSmartCommandFormatter<PbUpdateInfoSet.UpdateInfoSetResponse> {

    private URoUkitSmartUpdateInfoSetFormatter(){}

    public static final URoUkitSmartUpdateInfoSetFormatter INSTANCE = new URoUkitSmartUpdateInfoSetFormatter();

    @Override
    public URoResponse<PbUpdateInfoSet.UpdateInfoSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbUpdateInfoSet.UpdateInfoSetResponse data;
        try {
            PbUpdateInfoSet.UpdateInfoSetResponse response = PbUpdateInfoSet.UpdateInfoSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbUpdateInfoSet.UpdateInfoSetRequest.Builder builder = PbUpdateInfoSet.UpdateInfoSetRequest.newBuilder();
        builder.setDev(request.getParameter("dev", 0));
        builder.setPackageUrl(request.getParameter("packageUrl", ""));
        builder.setPackageMd5(request.getParameter("packageMd5", ""));
        builder.setPackageSize(request.getParameter("packageSize", 0));
        HashSet<Integer> collection = request.getParameter("ids", new HashSet<>());
        builder.addAllId(collection);
        return builder.build().toByteArray();
    }

}
