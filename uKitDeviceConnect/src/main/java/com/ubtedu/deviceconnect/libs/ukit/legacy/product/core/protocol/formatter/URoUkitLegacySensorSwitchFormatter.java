package com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter;

import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationParamKeys;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoUkitDummyData;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitLegacySensorSwitchFormatter extends URoUkitLegacyCommandFormatter<URoUkitDummyData> {

    private URoUkitLegacySensorSwitchFormatter(){}

    public static final URoUkitLegacySensorSwitchFormatter INSTANCE = new URoUkitLegacySensorSwitchFormatter();

    @Override
    public URoResponse<URoUkitDummyData> decodeResponseMessage(byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = bizData != null && bizData[2] == 0x00;
        URoUkitDummyData data = null;
        if(success) {
            data = URoUkitDummyData.INSTANCE;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        int[] ids = request.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_ID_ARRAY, null);
        boolean enable = request.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_ENABLE, false);
        int sensorType = request.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_TYPE, -1);
        return new byte[] { (byte)sensorType, getIdByte(ids), (byte)(enable ? 0x00 : 0x01) };
    }

}
