package com.ubtedu.deviceconnect.libs.ukit.smart.product.core.protocol.formatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.smart.protobuffer.PbHwVer;
import com.ubtedu.deviceconnect.libs.utils.URoLogUtils;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitSmartBoardBatteryFormatter extends URoUkitSmartCommandFormatter<PbHwVer.HwVerResponse> {

    private URoUkitSmartBoardBatteryFormatter(){}

    public static final URoUkitSmartBoardBatteryFormatter INSTANCE = new URoUkitSmartBoardBatteryFormatter();

    @Override
    public URoResponse<PbHwVer.HwVerResponse> decodeResponseMessage(int errorCode, byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = errorCode == 0;
        PbHwVer.HwVerResponse data;
        try {
            PbHwVer.HwVerResponse response = PbHwVer.HwVerResponse.parseFrom(bizData);
            URoLogUtils.d("HwVer: %s", response.getHwVer());
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
