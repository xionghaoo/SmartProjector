package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbWifiConnectSet;

/**
 * 
 * NOTICE: This file is generation by script.
 * 
 * @Author naOKi
 * @Date 2020/07/01
 * @CmdId 302
 * @FunctionName wifi_connect_set
 * @Description 设置WIFI连接
 * @FileName URoUkitSmartWifiConnectSetFormatter.java
 * 
 **/
 public class URoUkitSmartWifiConnectSetFormatter extends URoUkitSmartCommandFormatter<PbWifiConnectSet.WifiConnectSetResponse> {

    private URoUkitSmartWifiConnectSetFormatter(){}

    public static final URoUkitSmartWifiConnectSetFormatter INSTANCE = new URoUkitSmartWifiConnectSetFormatter();

    @Override
    public URoResponse<PbWifiConnectSet.WifiConnectSetResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbWifiConnectSet.WifiConnectSetResponse data;
        try {
            PbWifiConnectSet.WifiConnectSetResponse response = PbWifiConnectSet.WifiConnectSetResponse.parseFrom(bizData);
            data = response;
        } catch (InvalidProtocolBufferException e) {
            data = null;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        PbWifiConnectSet.WifiConnectSetRequest.Builder builder = PbWifiConnectSet.WifiConnectSetRequest.newBuilder();
        Object statusValue = request.getParameter("status", null);
        int status = 0;
        if(statusValue instanceof Boolean) {
            status = ((Boolean)statusValue) ? 1 : 0;
        } else if(statusValue instanceof Integer) {
            status = ((Integer)statusValue);
        }
        builder.setStatus(status);
        return builder.build().toByteArray();
    }

}
