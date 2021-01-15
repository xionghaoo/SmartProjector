package com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter;

import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationParamKeys;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoUkitDummyData;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitLegacyBoardSelfCheckFormatter extends URoUkitLegacyCommandFormatter<URoUkitDummyData> {

    private URoUkitLegacyBoardSelfCheckFormatter(){}

    public static final URoUkitLegacyBoardSelfCheckFormatter INSTANCE = new URoUkitLegacyBoardSelfCheckFormatter();

    @Override
    public URoResponse<URoUkitDummyData> decodeResponseMessage(byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = bizData != null && bizData[0] == 0x00;
        URoUkitDummyData data = null;
        if(success) {
            data = URoUkitDummyData.INSTANCE;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        boolean isEnabled = request.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_ENABLE, true);
        return new byte[] { (byte)(isEnabled ? 0x00 : 0x01) };
    }

}
