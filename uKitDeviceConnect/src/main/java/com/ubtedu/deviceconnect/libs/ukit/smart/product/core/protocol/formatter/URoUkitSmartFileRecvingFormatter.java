package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbFileRecving;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 107
 * @FunctionName file_recving
 * @Description 回传每帧文件包
 * @FileName URoUkitSmartFileRecvingFormatter.java
 * 
 **/
public class URoUkitSmartFileRecvingFormatter extends URoUkitSmartCommandFormatter<PbFileRecving.FileRecvingResponse> {

    private URoUkitSmartFileRecvingFormatter(){}

    public static final URoUkitSmartFileRecvingFormatter INSTANCE = new URoUkitSmartFileRecvingFormatter();

    @Override
    public URoResponse<PbFileRecving.FileRecvingResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbFileRecving.FileRecvingResponse data;
        try {
            PbFileRecving.FileRecvingResponse response = PbFileRecving.FileRecvingResponse.parseFrom(bizData);
            URoLogUtils.d("PackageIdx: %d", response.getPackIdx());
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbFileRecving.FileRecvingRequest.Builder builder = PbFileRecving.FileRecvingRequest.newBuilder();
        builder.setPackIdx(request.getParameter("pack_idx", 0));
        return builder.build().toByteArray();
    }

}
