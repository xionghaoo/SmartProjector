package com.ubtedu.deviceconnect.libs.ukit.legacy.product.core.protocol.formatter;

import com.ubtedu.deviceconnect.libs.base.invocation.constants.URoInvocationParamKeys;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoRequest;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoResponse;
import com.ubtedu.deviceconnect.libs.base.product.core.queue.URoUkitDummyData;

/**
 * @Author naOKi
 * @Date 2019/06/19
 **/
public class URoUkitLegacySensorModifyFormatter extends URoUkitLegacyCommandFormatter<URoUkitDummyData> {

    private URoUkitLegacySensorModifyFormatter(){}

    public static final URoUkitLegacySensorModifyFormatter INSTANCE = new URoUkitLegacySensorModifyFormatter();

    @Override
    public URoResponse<URoUkitDummyData> decodeResponseMessage(byte[] bizData, byte[] rawResponse) throws Exception {
        boolean success = bizData != null  && bizData.length == 3 && bizData[2] == 0x00;
        URoUkitDummyData data = null;
        if(success) {
            data = URoUkitDummyData.INSTANCE;
        }
        return URoResponse.newInstance(success, null, rawResponse, data);
    }

    @Override
    public byte[] encodeRequestMessage(URoRequest request) throws Exception {
        int sensorType = request.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_TYPE, -1);
        int srcId = request.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_SRC_ID, -1);
        int dstId = request.getParameter(URoInvocationParamKeys.Component.PARAM_KEY_NEW_ID, -1);
        return new byte[] { (byte)sensorType, (byte)srcId, (byte)dstId };
    }

}
