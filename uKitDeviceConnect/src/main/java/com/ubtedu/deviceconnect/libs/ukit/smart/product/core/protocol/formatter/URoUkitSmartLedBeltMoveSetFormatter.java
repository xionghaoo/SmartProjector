package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbLedBeltMoveSet;

import java.util.HashMap;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/04/30
 * @CmdId 1005
 * @FunctionName led_belt_move_set
 * @Description 灯带移动设置
 * @FileName URoUkitSmartLedBeltMoveSetFormatter.java
 * 
 **/
 public class URoUkitSmartLedBeltMoveSetFormatter extends URoUkitSmartCommandFormatter<PbLedBeltMoveSet.LedBeltMoveSetResponse> {

    private URoUkitSmartLedBeltMoveSetFormatter(){}

    public static final URoUkitSmartLedBeltMoveSetFormatter INSTANCE = new URoUkitSmartLedBeltMoveSetFormatter();

    @Override
    public URoResponse<PbLedBeltMoveSet.LedBeltMoveSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbLedBeltMoveSet.LedBeltMoveSetResponse data;
        try {
            PbLedBeltMoveSet.LedBeltMoveSetResponse response = PbLedBeltMoveSet.LedBeltMoveSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbLedBeltMoveSet.LedBeltMoveSetRequest.Builder builder = PbLedBeltMoveSet.LedBeltMoveSetRequest.newBuilder();
        for(int i = 0; i < 4; i++) {
            String key = "port" + i;
            HashMap<String, Integer> info = request.getParameter(key, null);
            if(info == null) {
                continue;
            }
            if(info.get("port") == null || info.get("pixel") == null || info.get("time") == null) {
                continue;
            }
            PbLedBeltMoveSet.LedBeltMoveSetRequest.info.Builder infoBuilder = PbLedBeltMoveSet.LedBeltMoveSetRequest.info.newBuilder();
            infoBuilder.setPort(info.get("port"));
            infoBuilder.setPixel(info.get("pixel"));
            infoBuilder.setTime(info.get("time"));
            builder.addMove(infoBuilder);
        }
        return builder.build().toByteArray();
    }

}
