package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbFileRecvMaxPackSize;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 105
 * @FunctionName file_recv_max_pack_size
 * @Description 获取回传文件时，每次最大上传包数据大小
 * @FileName URoUkitSmartFileRecvMaxPackSizeFormatter.java
 * 
 **/
public class URoUkitSmartFileRecvMaxPackSizeFormatter extends URoUkitSmartCommandFormatter<PbFileRecvMaxPackSize.FileRecvMaxPackSizeResponse> {

    private URoUkitSmartFileRecvMaxPackSizeFormatter(){}

    public static final URoUkitSmartFileRecvMaxPackSizeFormatter INSTANCE = new URoUkitSmartFileRecvMaxPackSizeFormatter();

    @Override
    public URoResponse<PbFileRecvMaxPackSize.FileRecvMaxPackSizeResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbFileRecvMaxPackSize.FileRecvMaxPackSizeResponse data;
        try {
            PbFileRecvMaxPackSize.FileRecvMaxPackSizeResponse response = PbFileRecvMaxPackSize.FileRecvMaxPackSizeResponse.parseFrom(bizData);
            URoLogUtils.d("MaxPackageSize: %d", response.getMaxPackSz());
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
