package com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter;

import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.legacy.model.URoUkitLegacyMainBoardInfo;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitLegacyBoardInfoFormatter extends URoUkitLegacyCommandFormatter<URoUkitLegacyMainBoardInfo> {

    private URoUkitLegacyBoardInfoFormatter(){}

    public static final URoUkitLegacyBoardInfoFormatter INSTANCE = new URoUkitLegacyBoardInfoFormatter();

    @Override
    public URoResponse<URoUkitLegacyMainBoardInfo> decodeResponseMessage(byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = bizData.length != 1;
        URoUkitLegacyMainBoardInfo data = null;
        if(success) {
            data = new URoUkitLegacyMainBoardInfo(bizData);
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        return EMPTY_REQUEST_DATA;
    }

}
