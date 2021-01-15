package com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter;

import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.legacy.model.URoUkitLegacyBatteryInfo;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitLegacyBoardBatteryFormatter extends URoUkitLegacyCommandFormatter<URoUkitLegacyBatteryInfo> {

    private URoUkitLegacyBoardBatteryFormatter(){}

    public static final URoUkitLegacyBoardBatteryFormatter INSTANCE = new URoUkitLegacyBoardBatteryFormatter();

    @Override
    public URoResponse<URoUkitLegacyBatteryInfo> decodeResponseMessage(byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = bizData.length != 1;
        URoUkitLegacyBatteryInfo data = null;
        if(success) {
            data = new URoUkitLegacyBatteryInfo(bizData);
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        return EMPTY_REQUEST_DATA;
    }

}
