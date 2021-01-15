package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbFileSendMaxPackSize;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 100
 * @FunctionName file_send_max_pack_size
 * @Description 获取下发文件到设备时，每次最大可以接受的分包数据大小
 * @FileName URoUkitSmartFileSendMaxPackSizeFormatter.java
 * 
 **/
public class URoUkitSmartFileSendMaxPackSizeFormatter extends URoUkitSmartCommandFormatter<PbFileSendMaxPackSize.FileSendMaxPackSizeResponse> {

    private URoUkitSmartFileSendMaxPackSizeFormatter(){}

    public static final URoUkitSmartFileSendMaxPackSizeFormatter INSTANCE = new URoUkitSmartFileSendMaxPackSizeFormatter();

    @Override
    public URoResponse<PbFileSendMaxPackSize.FileSendMaxPackSizeResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbFileSendMaxPackSize.FileSendMaxPackSizeResponse data;
        try {
            PbFileSendMaxPackSize.FileSendMaxPackSizeResponse response = PbFileSendMaxPackSize.FileSendMaxPackSizeResponse.parseFrom(bizData);
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
