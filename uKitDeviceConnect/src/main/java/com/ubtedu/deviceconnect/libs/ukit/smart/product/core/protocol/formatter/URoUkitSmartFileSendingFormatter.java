package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbFileSending;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 102
 * @FunctionName file_sending
 * @Description 下发每帧文件包
 * @FileName URoUkitSmartFileSendingFormatter.java
 * 
 **/
public class URoUkitSmartFileSendingFormatter extends URoUkitSmartCommandFormatter<PbFileSending.FileSendingResponse> {

    private URoUkitSmartFileSendingFormatter(){}

    public static final URoUkitSmartFileSendingFormatter INSTANCE = new URoUkitSmartFileSendingFormatter();

    @Override
    public URoResponse<PbFileSending.FileSendingResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbFileSending.FileSendingResponse data;
        try {
            PbFileSending.FileSendingResponse response = PbFileSending.FileSendingResponse.parseFrom(bizData);
            URoLogUtils.d("PackageIdx: %d", response.getPackIdx());
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbFileSending.FileSendingRequest.Builder builder = PbFileSending.FileSendingRequest.newBuilder();
        builder.setPackIdx(request.getParameter("pack_idx", 0));
        builder.setData(ByteString.copyFrom(request.getParameter("data", new byte[0])));
        return builder.build().toByteArray();
    }

}
