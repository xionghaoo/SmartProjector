package com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter;

import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitLegacyBoardHandshakeFormatter extends URoUkitLegacyCommandFormatter<String> {

    private URoUkitLegacyBoardHandshakeFormatter(){}

    public static final URoUkitLegacyBoardHandshakeFormatter INSTANCE = new URoUkitLegacyBoardHandshakeFormatter();

    @Override
    public URoResponse<String> decodeResponseMessage(byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = bizData != null;
        String data = null;
        if(success) {
            data = new String(bizData);
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        return EMPTY_REQUEST_DATA;
    }

}
