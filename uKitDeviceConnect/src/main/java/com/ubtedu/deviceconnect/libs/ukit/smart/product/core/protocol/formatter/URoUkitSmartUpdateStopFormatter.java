package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbUpdateStop;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2019/08/10
 * @CmdId: 154
 * @FunctionName：update_stop
 * @Description：强制结束升级
 * @FileName: URoUkitSmartUpdateStopFormatter.java
 * 
 **/
public class URoUkitSmartUpdateStopFormatter extends URoUkitSmartCommandFormatter<PbUpdateStop.UpdateStopResponse> {

    private URoUkitSmartUpdateStopFormatter(){}

    public static final URoUkitSmartUpdateStopFormatter INSTANCE = new URoUkitSmartUpdateStopFormatter();

    @Override
    public URoResponse<PbUpdateStop.UpdateStopResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbUpdateStop.UpdateStopResponse data;
        try {
            PbUpdateStop.UpdateStopResponse response = PbUpdateStop.UpdateStopResponse.parseFrom(bizData);
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
