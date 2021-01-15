package com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter;

import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.ukit.legacy.model.URoUkitLegacyComponentMap;
import com.ubtedu.deviceconnect.libs.ukit.legacy.model.URoUkitLegacySerialNumberInfo;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitLegacyBoardSerialNumberFormatter extends URoUkitLegacyCommandFormatter<URoUkitLegacySerialNumberInfo> {

    private URoUkitLegacyBoardSerialNumberFormatter(){}

    public static final URoUkitLegacyBoardSerialNumberFormatter INSTANCE = new URoUkitLegacyBoardSerialNumberFormatter();

    @Override
    public URoResponse<URoUkitLegacySerialNumberInfo> decodeResponseMessage(byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = bizData.length != 1;
        URoUkitLegacySerialNumberInfo data = null;
        if(success) {
            data = new URoUkitLegacySerialNumberInfo(bizData);
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        return new byte[] { (byte)0x07 };
    }

}
