package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbDevUpdateStop;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/09/10
 * @CmdId: 262
 * @FunctionName：dev_update_stop
 * @Description：停止设备升级
 * @FileName: URoUkitSmartDevUpdateStopFormatter.java
 * 
 **/
public class URoUkitSmartDevUpdateStopFormatter extends URoUkitSmartCommandFormatter<PbDevUpdateStop.DevUpdateStopResponse> {

    private URoUkitSmartDevUpdateStopFormatter(){}

    public static final URoUkitSmartDevUpdateStopFormatter INSTANCE = new URoUkitSmartDevUpdateStopFormatter();

    @Override
    public URoResponse<PbDevUpdateStop.DevUpdateStopResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbDevUpdateStop.DevUpdateStopResponse data;
        try {
            PbDevUpdateStop.DevUpdateStopResponse response = PbDevUpdateStop.DevUpdateStopResponse.parseFrom(bizData);
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
