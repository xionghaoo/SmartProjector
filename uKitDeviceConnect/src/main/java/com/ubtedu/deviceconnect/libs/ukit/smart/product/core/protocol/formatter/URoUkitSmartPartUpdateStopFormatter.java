package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbPartSwVer;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbPartUpdateStop;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/09/10
 * @CmdId: 211
 * @FunctionName：part_update_stop
 * @Description：停止部件升级
 * @FileName: URoUkitSmartPartUpdateStopFormatter.java
 * 
 **/
public class URoUkitSmartPartUpdateStopFormatter extends URoUkitSmartCommandFormatter<PbPartUpdateStop.PartUpdateStopResponse> {

    private URoUkitSmartPartUpdateStopFormatter(){}

    public static final URoUkitSmartPartUpdateStopFormatter INSTANCE = new URoUkitSmartPartUpdateStopFormatter();

    @Override
    public URoResponse<PbPartUpdateStop.PartUpdateStopResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbPartUpdateStop.PartUpdateStopResponse data;
        try {
            PbPartUpdateStop.PartUpdateStopResponse response = PbPartUpdateStop.PartUpdateStopResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbPartSwVer.PartSwVerRequest.Builder builder = PbPartSwVer.PartSwVerRequest.newBuilder();
        builder.setPart(request.getParameter("part", 0));
        return builder.build().toByteArray();
    }

}
