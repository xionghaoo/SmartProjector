package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbFileSendStop;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbResetStateSet;

/**
 *
 * @Author xinning.duan
 * @Date 2020/03/17
 * @CmdId 26
 * @FunctionName reset_state_set
 * @Description 重置状态设置
 * @FileName URoUkitSmartResetStateSetFormatter.java
 * 
 **/
public class URoUkitSmartResetStateSetFormatter extends URoUkitSmartCommandFormatter<PbResetStateSet.ResetStateSetResponse> {

    private URoUkitSmartResetStateSetFormatter(){}

    public static final URoUkitSmartResetStateSetFormatter INSTANCE = new URoUkitSmartResetStateSetFormatter();

    @Override
    public URoResponse<PbResetStateSet.ResetStateSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbResetStateSet.ResetStateSetResponse data;
        try {
            PbResetStateSet.ResetStateSetResponse response = PbResetStateSet.ResetStateSetResponse.parseFrom(bizData);
            // TODO check the response
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbResetStateSet.ResetStateSetRequest.Builder builder = PbResetStateSet.ResetStateSetRequest.newBuilder();
        /* TODO set request params to pb
        builder.setPath(request.getParameter("path", ""));
        */
        return builder.build().toByteArray();
    }

}
