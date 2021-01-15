package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbSubtypeSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId: 19
 * @FunctionName：subtype_set
 * @Description：设置传感器子类型
 * @FileName: URoUkitSmartSubtypeSetFormatter.java
 * 
 **/
public class URoUkitSmartSubtypeSetFormatter extends URoUkitSmartCommandFormatter<PbSubtypeSet.SubtypeSetResponse> {

    private URoUkitSmartSubtypeSetFormatter(){}

    public static final URoUkitSmartSubtypeSetFormatter INSTANCE = new URoUkitSmartSubtypeSetFormatter();

    @Override
    public URoResponse<PbSubtypeSet.SubtypeSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbSubtypeSet.SubtypeSetResponse data;
        try {
            PbSubtypeSet.SubtypeSetResponse response = PbSubtypeSet.SubtypeSetResponse.parseFrom(bizData);
            // TODO check the response
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbSubtypeSet.SubtypeSetRequest.Builder builder = PbSubtypeSet.SubtypeSetRequest.newBuilder();
        /* TODO set request params to pb
        builder.setPath(request.getParameter("path", ""));
        */
        return builder.build().toByteArray();
    }

}
