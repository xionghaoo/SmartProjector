package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbFileSendStop;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId 104
 * @FunctionName file_send_stop
 * @Description 强制结束下发文件
 * @FileName URoUkitSmartFileSendStopFormatter.java
 * 
 **/
public class URoUkitSmartFileSendStopFormatter extends URoUkitSmartCommandFormatter<PbFileSendStop.FileSendStopResponse> {

    private URoUkitSmartFileSendStopFormatter(){}

    public static final URoUkitSmartFileSendStopFormatter INSTANCE = new URoUkitSmartFileSendStopFormatter();

    @Override
    public URoResponse<PbFileSendStop.FileSendStopResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbFileSendStop.FileSendStopResponse data;
        try {
            PbFileSendStop.FileSendStopResponse response = PbFileSendStop.FileSendStopResponse.parseFrom(bizData);
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
