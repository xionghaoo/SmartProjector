package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbIdSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/22
 * @CmdId 201
 * @FunctionName id_set
 * @Description 修改id
 * @FileName URoUkitSmartIdSetFormatter.java
 * 
 **/
public class URoUkitSmartIdSetFormatter extends URoUkitSmartCommandFormatter<PbIdSet.IdSetResponse> {

    private URoUkitSmartIdSetFormatter(){}

    public static final URoUkitSmartIdSetFormatter INSTANCE = new URoUkitSmartIdSetFormatter();

    @Override
    public URoResponse<PbIdSet.IdSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbIdSet.IdSetResponse data;
        try {
            PbIdSet.IdSetResponse response = PbIdSet.IdSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbIdSet.IdSetRequest.Builder builder = PbIdSet.IdSetRequest.newBuilder();
        builder.setNewId(request.getParameter("new_id", 0));
        return builder.build().toByteArray();
    }

}
