package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbFileRecvOver;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 108
 * @FunctionName file_recv_over
 * @Description 回传文件完成
 * @FileName URoUkitSmartFileRecvOverFormatter.java
 * 
 **/
public class URoUkitSmartFileRecvOverFormatter extends URoUkitSmartCommandFormatter<PbFileRecvOver.FileRecvOverResponse> {

    private URoUkitSmartFileRecvOverFormatter(){}

    public static final URoUkitSmartFileRecvOverFormatter INSTANCE = new URoUkitSmartFileRecvOverFormatter();

    @Override
    public URoResponse<PbFileRecvOver.FileRecvOverResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbFileRecvOver.FileRecvOverResponse data;
        try {
            PbFileRecvOver.FileRecvOverResponse response = PbFileRecvOver.FileRecvOverResponse.parseFrom(bizData);
            URoLogUtils.d("FileSize: %d", response.getFileSize());
            URoLogUtils.d("Crc32: %d", response.getCrc32());
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        return EMPTY_REQUEST_DATA;
    }

}
